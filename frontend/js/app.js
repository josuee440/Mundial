
const API = 'http://localhost:8080/api';
let _equipos=[], _jugadores=[], _estadios=[], _partidos=[], _goles=[], _tarjetas=[], _posiciones=[];
let FASES = ['DIECISEISAVOS','OCTAVOS DE FINAL','CUARTOS DE FINAL','SEMIFINAL','FINAL'];
let faseIdx=0, rondas=[], campeón=null, gruposSimulados={}, gruposSimulado=false;

function mostrar(id, btn) { document.querySelectorAll('.seccion').forEach(s=>s.classList.remove('activa')); document.querySelectorAll('nav button').forEach(b=>b.classList.remove('active')); document.getElementById('sec-'+id).classList.add('activa'); btn.classList.add('active'); if(id==='equipos')renderEquipos(); if(id==='jugadores')renderJugadores(); if(id==='estadios')renderEstadios(); if(id==='entrenadores')renderEntrenadores(); if(id==='grupos')renderGrupos(); if(id==='partidos')renderPartidos(); if(id==='simulador')renderBracket(); if(id==='reportes')renderReportes(); }
function irAFase(v) { document.getElementById('vista-grupos-sim').style.display=v==='grupos-sim'?'block':'none'; document.getElementById('vista-eliminacion').style.display=v==='eliminacion'?'block':'none'; }
async function get(ruta) { try { const r=await fetch(API+ruta); return await r.json(); } catch(e) { toast('Error: '+ruta, true); return []; } }
function toast(m, e=false) { const t=document.getElementById('toast'); t.textContent=m; t.className='show'+(e?' error':''); setTimeout(()=>t.className='',3200); }

async function cargarDashboard() {
  [_equipos,_jugadores,_estadios,_partidos,_goles,_tarjetas,_posiciones] = await Promise.all([get('/equipos'),get('/jugadores'),get('/estadios'),get('/partidos'),get('/goles'),get('/tarjetas'),get('/posiciones')]);
  document.getElementById('cnt-equipos').textContent=_equipos.length; document.getElementById('cnt-jugadores').textContent=_jugadores.length; document.getElementById('cnt-estadios').textContent=_estadios.length; document.getElementById('cnt-partidos').textContent=_partidos.length; document.getElementById('cnt-goles').textContent=_goles.length; document.getElementById('cnt-tarjetas').textContent=_tarjetas.length;
}

// ── CRUD EQUIPOS ──
function renderEquipos() {
  const tb = document.getElementById('tb-equipos'); if(!_equipos.length){ tb.innerHTML='<tr><td colspan="4" class="loading">Sin datos</td></tr>'; return; }
  tb.innerHTML = _equipos.map((e,i) => `<tr><td>${i+1}</td><td><strong>${e.pais||e.nombre}</strong></td><td><span class="badge badge-grupo">Grupo ${e.grupo||'—'}</span></td><td style="text-align: right;"><button class="btn btn-oro" style="padding:6px 12px;font-size:0.7rem;" onclick='editarEquipo(${JSON.stringify(e).replace(/'/g,"&apos;")})'>✏️</button> <button class="btn btn-rojo" style="padding:6px 12px;font-size:0.7rem;" onclick="eliminarEquipo(${e.idEquipo||e.idequipo})">🗑️</button></td></tr>`).join('');
}
function abrirModalEquipo() { document.getElementById('modal-equipo-titulo').innerText='NUEVO EQUIPO'; document.getElementById('eq-id').value=''; document.getElementById('eq-pais').value=''; document.getElementById('eq-grupo').value=''; document.getElementById('eq-idgrupo').value=''; document.getElementById('modal-equipo').style.display='flex'; }
function cerrarModalEquipo() { document.getElementById('modal-equipo').style.display='none'; }
function editarEquipo(e) { document.getElementById('modal-equipo-titulo').innerText='EDITAR EQUIPO'; document.getElementById('eq-id').value=e.idEquipo||e.idequipo; document.getElementById('eq-pais').value=e.pais||e.nombre; document.getElementById('eq-grupo').value=e.grupo; document.getElementById('eq-idgrupo').value=e.idGrupo||''; document.getElementById('modal-equipo').style.display='flex'; }
async function guardarEquipo() { const id=document.getElementById('eq-id').value, data={ id_equipo:id?parseInt(id):0, pais:document.getElementById('eq-pais').value, grupo:document.getElementById('eq-grupo').value, id_grupo:parseInt(document.getElementById('eq-idgrupo').value)||0 }; try { await fetch(API+'/equipos', {method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalEquipo(); _equipos=await get('/equipos'); renderEquipos(); document.getElementById('cnt-equipos').textContent=_equipos.length; toast('Guardado'); } catch(e){} }
async function eliminarEquipo(id) { if(confirm('¿Eliminar equipo?')){ await fetch(API+'/equipos', {method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_equipo:id})}); _equipos=await get('/equipos'); renderEquipos(); document.getElementById('cnt-equipos').textContent=_equipos.length; toast('Eliminado'); } }

// ── CRUD JUGADORES ──
function renderJugadores(){ filtrarJugadores(); }
function filtrarJugadores(){
  const q=document.getElementById('buscar-jugador').value.toLowerCase(), tb=document.getElementById('tb-jugadores'), arr=_jugadores.filter(j=>j.nombre.toLowerCase().includes(q)||(j.paisEquipo||'').toLowerCase().includes(q));
  if(!arr.length){tb.innerHTML='<tr><td colspan="6" class="loading">Sin resultados</td></tr>';return;}
  tb.innerHTML=arr.map((j,i)=> `<tr><td>${i+1}</td><td><strong>${j.nombre}</strong></td><td>${j.posicion}</td><td><strong>${j.dorsal}</strong></td><td>${j.paisEquipo||'—'}</td><td style="text-align: right;"><button class="btn btn-oro" style="padding:6px 12px;font-size:0.7rem;" onclick='editarJugador(${JSON.stringify(j).replace(/'/g,"&apos;")})'>✏️</button> <button class="btn btn-rojo" style="padding:6px 12px;font-size:0.7rem;" onclick="eliminarJugador(${j.idJugador||j.idjugador})">🗑️</button></td></tr>`).join('');
}
function cargarSelectEquipos(){ document.getElementById('jug-idequipo').innerHTML='<option value="">-- Selecciona --</option>'+_equipos.map(e=>`<option value="${e.idEquipo||e.idequipo}">${e.pais||e.nombre}</option>`).join(''); }
function abrirModalJugador(){ cargarSelectEquipos(); document.getElementById('modal-jugador-titulo').innerText='NUEVO JUGADOR'; document.getElementById('jug-id').value=''; document.getElementById('jug-nombre').value=''; document.getElementById('jug-posicion').value='Delantero'; document.getElementById('jug-dorsal').value=''; document.getElementById('jug-idequipo').value=''; document.getElementById('modal-jugador').style.display='flex'; }
function cerrarModalJugador(){ document.getElementById('modal-jugador').style.display='none'; }
function editarJugador(j){ cargarSelectEquipos(); document.getElementById('modal-jugador-titulo').innerText='EDITAR JUGADOR'; document.getElementById('jug-id').value=j.idJugador||j.idjugador; document.getElementById('jug-nombre').value=j.nombre; document.getElementById('jug-posicion').value=j.posicion; document.getElementById('jug-dorsal').value=j.dorsal; document.getElementById('jug-idequipo').value=j.idEquipo||j.idequipo; document.getElementById('modal-jugador').style.display='flex'; }
async function guardarJugador(){ const id=document.getElementById('jug-id').value, data={ id_jugador:id?parseInt(id):0, nombre:document.getElementById('jug-nombre').value, posicion:document.getElementById('jug-posicion').value, dorsal:parseInt(document.getElementById('jug-dorsal').value)||0, id_equipo:parseInt(document.getElementById('jug-idequipo').value)||0 }; try { await fetch(API+'/jugadores',{method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalJugador(); _jugadores=await get('/jugadores'); renderJugadores(); document.getElementById('cnt-jugadores').textContent=_jugadores.length; toast('Guardado'); } catch(e){} }
async function eliminarJugador(id){ if(confirm('¿Eliminar jugador?')){ await fetch(API+'/jugadores',{method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_jugador:id})}); _jugadores=await get('/jugadores'); renderJugadores(); document.getElementById('cnt-jugadores').textContent=_jugadores.length; toast('Eliminado'); } }

// ── CRUD ESTADIOS ──
function renderEstadios() {
  const tb = document.getElementById('tb-estadios'); if(!_estadios.length){ tb.innerHTML='<tr><td colspan="6" class="loading">Sin datos</td></tr>'; return; }
  tb.innerHTML = _estadios.map((e,i) => `<tr><td>${i+1}</td><td><strong>${e.nombre}</strong></td><td>${e.ciudad}</td><td>${e.pais}</td><td>${(e.capacidad||0).toLocaleString()}</td><td style="text-align: right;"><button class="btn btn-oro" style="padding:6px 12px;font-size:0.7rem;" onclick='editarEstadio(${JSON.stringify(e).replace(/'/g,"&apos;")})'>✏️</button> <button class="btn btn-rojo" style="padding:6px 12px;font-size:0.7rem;" onclick="eliminarEstadio(${e.idEstadio||e.idestadio})">🗑️</button></td></tr>`).join('');
}
function abrirModalEstadio() { document.getElementById('modal-estadio-titulo').innerText='NUEVO ESTADIO'; document.getElementById('est-id').value=''; document.getElementById('est-nombre').value=''; document.getElementById('est-ciudad').value=''; document.getElementById('est-pais').value=''; document.getElementById('est-capacidad').value=''; document.getElementById('modal-estadio').style.display='flex'; }
function cerrarModalEstadio() { document.getElementById('modal-estadio').style.display='none'; }
function editarEstadio(e) { document.getElementById('modal-estadio-titulo').innerText='EDITAR ESTADIO'; document.getElementById('est-id').value=e.idEstadio||e.idestadio; document.getElementById('est-nombre').value=e.nombre; document.getElementById('est-ciudad').value=e.ciudad; document.getElementById('est-pais').value=e.pais; document.getElementById('est-capacidad').value=e.capacidad||''; document.getElementById('modal-estadio').style.display='flex'; }
async function guardarEstadio() { const id=document.getElementById('est-id').value, data={ id_estadio:id?parseInt(id):0, nombre:document.getElementById('est-nombre').value, ciudad:document.getElementById('est-ciudad').value, pais:document.getElementById('est-pais').value, capacidad:parseInt(document.getElementById('est-capacidad').value)||0 }; try { await fetch(API+'/estadios', {method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalEstadio(); _estadios=await get('/estadios'); renderEstadios(); document.getElementById('cnt-estadios').textContent=_estadios.length; toast('Estadio guardado'); } catch(e){} }
async function eliminarEstadio(id) { if(confirm('¿Eliminar estadio?')){ await fetch(API+'/estadios', {method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_estadio:id})}); _estadios=await get('/estadios'); renderEstadios(); document.getElementById('cnt-estadios').textContent=_estadios.length; toast('Eliminado'); } }

// ── VISTAS DE LECTURA ──
function renderEntrenadores(){ const tb=document.getElementById('tb-entrenadores'); get('/entrenadores').then(d=>{ tb.innerHTML=d.length?d.map((e,i)=>`<tr><td>${i+1}</td><td><strong>${e.nombre||'—'}</strong></td><td>${e.paisEquipo||'—'}</td></tr>`).join(''):'<tr><td colspan="3">Sin datos</td></tr>'; }); }
function renderGrupos(){ const cont=document.getElementById('grupos-container'); if(!_posiciones.length){cont.innerHTML='<div class="loading">Sin posiciones</div>';return;} const gs={}; _posiciones.forEach(p=>{ const grp=p.grupo||(_equipos.find(e=>e.idEquipo===p.idEquipo||e.id===p.idEquipo)||{}).grupo||'?'; if(!gs[grp])gs[grp]=[]; gs[grp].push(p); }); Object.values(gs).forEach(a=>a.sort((x,y)=>y.puntos-x.puntos||(y.gf-y.gc)-(x.gf-x.gc))); cont.innerHTML=Object.entries(gs).sort().map(([g,pos])=>`<div class="grupo-card"><div class="grupo-header">GRUPO ${g}</div><table class="grupo-table"><thead><tr><th>País</th><th>PJ</th><th>PG</th><th>PE</th><th>PP</th><th>GF</th><th>GC</th><th>Pts</th></tr></thead><tbody>${pos.map((p,i)=>`<tr><td class="${i<2?'clasificado':''}">${p.pais}</td><td>${p.pj}</td><td>${p.pg}</td><td>${p.pe}</td><td>${p.pp}</td><td>${p.gf}</td><td>${p.gc}</td><td><strong>${p.puntos}</strong></td></tr>`).join('')}</tbody></table></div>`).join(''); }
function renderPartidos(){ document.getElementById('tb-partidos').innerHTML=_partidos.length?_partidos.map(p=>`<tr><td>${p.idPartido}</td><td><span class="badge badge-fase">${p.fase}</span></td><td><strong>${p.local||'—'}</strong></td><td>${p.visitante||'—'}</td><td><strong>${p.golesLocales} — ${p.golesVisitantes}</strong></td><td>${p.estadio||'—'}</td></tr>`).join(''):'<tr><td colspan="6">Sin datos</td></tr>'; }

// ── SIMULADOR ──
async function simularFaseGrupos() {
  if(!_equipos.length) return toast('Carga equipos primero', true);
  document.getElementById('estado-grupos').textContent='Calculando en Java...';
  
  await fetch(API+'/simulador/grupos', {method:'POST'});
  _partidos = await get('/partidos'); _posiciones = await get('/posiciones');
  
  const ag = {};
  _posiciones.forEach(p => {
    const grp = (_equipos.find(e => e.idEquipo === p.idEquipo || e.idequipo === p.idEquipo) || {}).grupo || '?';
    if(!ag[grp]) ag[grp] = [];
    ag[grp].push({ idEquipo: p.idEquipo, pais: p.pais, pts: p.puntos, pj: p.pj, gf: p.gf, gc: p.gc });
  });
  
  gruposSimulados = {};
  Object.entries(ag).forEach(([g, eqs]) => { gruposSimulados[g] = eqs.sort((a,b) => b.pts - a.pts || (b.gf - b.gc) - (a.gf - a.gc) || b.gf - a.gf); });
  
  gruposSimulado=true; document.getElementById('estado-grupos').textContent='¡Completado!';
  document.getElementById('grupos-sim-container').innerHTML = Object.entries(gruposSimulados).sort().map(([g,e]) => `<div class="grupo-sim-card"><div class="grupo-sim-header">GRUPO ${g}</div><table class="grupo-table"><thead><tr><th>País</th><th>Pts</th></tr></thead><tbody>${e.map((x,i)=>`<tr><td style="color:${i<2?'var(--verde)':'gray'}">${x.pais}</td><td>${x.pts}</td></tr>`).join('')}</tbody></table></div>`).join('');
  setTimeout(() => { irAFase('eliminacion'); faseIdx=0; rondas=[]; campeón=null; iniciarRonda0(); renderBracket(); toast('Clasificados listos'); }, 1500);
}

function iniciarRonda0() {
  let cl=[];
  if(gruposSimulado) Object.values(gruposSimulados).forEach(a=>{ if(a[0]) cl.push(a[0]); if(a[1]) cl.push(a[1]); });
  cl = cl.slice(0,32); cl.sort(()=>Math.random()-0.5); rondas=[[]];
  for(let i=0; i<cl.length; i+=2) rondas[0].push({ idLocal: (cl[i]||{}).idEquipo||0, local: (cl[i]||{}).pais||'—', idVisitante: (cl[i+1]||{}).idEquipo||0, visitante: (cl[i+1]||{}).pais||'—', golesL: null, golesV: null, ganador: null });
}

async function simularFase() {
  if(!rondas.length) iniciarRonda0(); if(faseIdx >= FASES.length) return toast('Torneo Finalizado');
  
  // Mandamos a Java a simular las llaves actuales simultáneamente
  const promesas = rondas[faseIdx].map(async m => {
    if(m.ganador) return m;
    const res = await fetch(API+'/simulador/partido', { method: 'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ fase: FASES[faseIdx], idLocal: m.idLocal, idVisitante: m.idVisitante, eliminatoria: true }) }).then(r=>r.json());
    m.golesL = res.golesL; m.golesV = res.golesV; m.idGanador = res.idGanador; m.ganador = res.idGanador === m.idLocal ? m.local : m.visitante;
    return m;
  });
  
  await Promise.all(promesas);
  const gan = rondas[faseIdx].map(m => ({ idEquipo: m.idGanador, pais: m.ganador })); faseIdx++;
  
  if(gan.length === 1) {
    campeón = gan[0].pais; document.getElementById('fase-actual').textContent='🏆 '+campeón; toast('Campeón: '+campeón);
  } else {
    document.getElementById('fase-actual').textContent=FASES[faseIdx]||'—'; const n=[];
    for(let i=0; i<gan.length; i+=2) n.push({ idLocal: gan[i].idEquipo, local: gan[i].pais, idVisitante: gan[i+1].idEquipo, visitante: gan[i+1].pais, golesL: null, golesV: null, ganador: null });
    rondas.push(n);
  }
  renderBracket();
}

async function simularTodo() { resetSimulador(); iniciarRonda0(); while(!campeón) await simularFase(); setTimeout(async ()=>{_partidos=await get('/partidos');_posiciones=await get('/posiciones');toast('Actualizado');},1000); }
function resetSimulador(){ faseIdx=0;rondas=[];campeón=null;document.getElementById('fase-actual').textContent=FASES[0];renderBracket(); }
function resetTodo(){ gruposSimulados={}; gruposSimulado=false; document.getElementById('grupos-sim-container').innerHTML='<div class="loading">...</div>'; document.getElementById('estado-grupos').textContent=''; resetSimulador(); irAFase('grupos-sim'); }
function renderBracket(){ document.getElementById('bracket').innerHTML=rondas.map((r,i)=>`<div class="ronda"><div class="ronda-titulo">${FASES[i]||''}</div><div class="ronda-partidos">${r.map(m=>`<div class="match-card"><div class="match-equipo ${m.ganador===m.local?'ganador':m.ganador?'perdedor':''}"><span>${m.local}</span>${m.golesL!==null?`<span class="gol-badge">${m.golesL}</span>`:''}</div><div class="match-equipo ${m.ganador===m.visitante?'ganador':m.ganador?'perdedor':''}"><span>${m.visitante}</span>${m.golesV!==null?`<span class="gol-badge">${m.golesV}</span>`:''}</div></div>`).join('')}</div></div>`).join(''); }
async function limpiarBD(){ if(confirm('¿Borrar TODO el historial?')){ await fetch(API+'/limpiar',{method:'POST',body:'{}'}); _partidos=[]; _posiciones=[]; document.getElementById('cnt-partidos').textContent='0'; resetTodo(); toast('Historial en blanco'); } }

// ── REPORTES Y GUANTE DE ORO ──
async function renderReportes(){
  _goles = await get('/goles'); _tarjetas = await get('/tarjetas'); _partidos = await get('/partidos');
  if(campeón){ document.getElementById('campeon-box').style.display='block'; document.getElementById('campeon-nombre').textContent=campeón; }
  
  // Pichichi
  const cg={}; _goles.forEach(g=>{const k=g.jugador||('#'+g.idJugador); cg[k]=(cg[k]||0)+1;});
  renderPodio('podio-goleadores',Object.entries(cg).sort((a,b)=>b[1]-a[1]).slice(0,5),'goles');

  // Tarjetas
  const ct={}; _tarjetas.forEach(t=>{const k=t.jugador||('#'+t.idJugador); ct[k]=(ct[k]||0)+1;});
  renderPodio('podio-tarjetas',Object.entries(ct).sort((a,b)=>b[1]-a[1]).slice(0,5),'tarjetas');

  // Goles por País
  const cp={}; _goles.forEach(g=>{ const j=_jugadores.find(x=>x.idJugador===g.idJugador); const p=j?j.paisEquipo:'—'; cp[p]=(cp[p]||0)+1; });
  renderPodio('podio-paises',Object.entries(cp).sort((a,b)=>b[1]-a[1]).slice(0,5),'goles');

  // 🧤 MEJOR PORTERO (Cálculo desde Controlador Java)
  const porterosJava = await get('/reportes/porteros');
  let statsPorteros = porterosJava.map(p => [p.nombre, p.promedio.toFixed(2)]);
  
  // Ordenar de MENOR promedio a MAYOR
  statsPorteros.sort((a, b) => parseFloat(a[1]) - parseFloat(b[1]));
  renderPodio('podio-porteros', statsPorteros.slice(0, 3), 'goles x partido');
}

function renderPodio(id,data,ud){ document.getElementById(id).innerHTML=data.length?data.map(([n,v],i)=>`<div class="podio-item"><div style="font-size:1.5rem;width:30px">${i===0?'🥇':i===1?'🥈':i===2?'🥉':(i+1)}</div><div style="flex:1">${n}</div><div class="podio-val">${v} <span style="font-size:0.6rem;color:var(--gris)">${ud}</span></div></div>`).join(''):'<div class="loading">Sin datos suficientes</div>'; }

window.addEventListener('DOMContentLoaded', cargarDashboard);
