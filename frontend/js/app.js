
const API = 'http://localhost:8080/api';
let _equipos=[], _jugadores=[], _estadios=[], _partidos=[], _goles=[], _tarjetas=[], _posiciones=[];
let FASES = ['DIECISEISAVOS DE FINAL','OCTAVOS DE FINAL','CUARTOS DE FINAL','SEMIFINAL','FINAL'];
let faseIdx=0, rondas=[], campeón=null, gruposSimulados={}, gruposSimulado=false;

function mostrar(id) { 
  const sec = document.getElementById('sec-' + id);
  if (sec) {
    // Si la sección existe en la página actual, la mostramos
    document.querySelectorAll('.seccion').forEach(s => s.classList.remove('activa')); 
    sec.classList.add('activa'); 
    if (id === 'equipos') renderEquipos(); 
    if (id === 'jugadores') renderJugadores(); 
    if (id === 'estadios') renderEstadios(); 
    if (id === 'entrenadores') renderEntrenadores(); 
    if (id === 'grupos') renderGrupos(); 
    if (id === 'partidos') renderPartidos(); 
    if (id === 'simulador') renderBracket(); 
    if (id === 'estadisticas') renderEstadisticas(); 
  } else {
    // Si la sección NO está en esta página, navegamos al archivo respectivo
    if (id === 'dashboard') window.location.href = 'index.html';
    else if (id === 'simulador') window.location.href = 'simulacion.html';
    else window.location.href = id + '.html';
  }
}
function irAFase(v) { document.getElementById('vista-grupos-sim').style.display=v==='grupos-sim'?'block':'none'; document.getElementById('vista-eliminacion').style.display=v==='eliminacion'?'block':'none'; }
async function get(ruta) { try { const r=await fetch(API+ruta); return await r.json(); } catch(e) { toast('Error: '+ruta, true); return []; } }
function toast(m, e=false) { const t=document.getElementById('toast'); t.textContent=m; t.className='show'+(e?' error':''); setTimeout(()=>t.className='',3200); }

// ── UTILIDAD BANDERAS REALES ──
function getFlagHtml(p, isSmall = false) { 
  if(!p || p === '—') return '';
  const d={'argentina':'ar','brasil':'br','méxico':'mx','mexico':'mx','estados unidos':'us','usa':'us','alemania':'de','francia':'fr','españa':'es','inglaterra':'gb-eng','italia':'it','uruguay':'uy','colombia':'co','chile':'cl','ecuador':'ec','perú':'pe','peru':'pe','venezuela':'ve','paraguay':'py','bolivia':'bo','canadá':'ca','canada':'ca','portugal':'pt','países bajos':'nl','paises bajos':'nl','holanda':'nl','bélgica':'be','belgica':'be','croacia':'hr','suiza':'ch','dinamarca':'dk','suecia':'se','polonia':'pl','japón':'jp','japon':'jp','corea del sur':'kr','corea':'kr','corea del norte':'kp','australia':'au','irán':'ir','iran':'ir','arabia saudita':'sa','senegal':'sn','ghana':'gh','camerún':'cm','camerun':'cm','marruecos':'ma','túnez':'tn','tunez':'tn','egipto':'eg','argelia':'dz','nigeria':'ng','guatemala':'gt','costa rica':'cr','honduras':'hn','el salvador':'sv','panamá':'pa','panama':'pa','congo':'cd','el congo':'cd','república democrática del congo':'cd','costa de marfil':'ci','sudáfrica':'za','sudafrica':'za','gales':'gb-wls','escocia':'gb-sct','grecia':'gr','serbia':'rs','bosnia':'ba','nueva zelanda':'nz','jamaica':'jm','qatar':'qa','catar':'qa','turquía':'tr','turquia':'tr','rusia':'ru','ucrania':'ua','mali':'ml','malí':'ml'}; 
  const code = d[p.toLowerCase().trim()]; 
  return code ? `<img src="https://flagcdn.com/w80/${code}.png" class="flag-img ${isSmall ? 'small' : ''}" alt="${p}">` : ''; 
}

async function cargarDashboard() {
  [_equipos,_jugadores,_estadios,_partidos,_goles,_tarjetas,_posiciones] = await Promise.all([get('/equipos'),get('/jugadores'),get('/estadios'),get('/partidos'),get('/goles'),get('/tarjetas'),get('/posiciones')]);
  
  // Validamos que los contenedores existan antes de actualizarlos (para evitar errores en múltiples páginas)
  if(document.getElementById('cnt-equipos')) document.getElementById('cnt-equipos').textContent=_equipos.length; 
  if(document.getElementById('cnt-jugadores')) document.getElementById('cnt-jugadores').textContent=_jugadores.length; 
  if(document.getElementById('cnt-estadios')) document.getElementById('cnt-estadios').textContent=_estadios.length; 
  if(document.getElementById('cnt-partidos')) document.getElementById('cnt-partidos').textContent=_partidos.length;
  
  // Auto-renderizar si estamos en la página de equipos
  if(document.getElementById('sec-equipos') && document.getElementById('sec-equipos').classList.contains('activa')) {
    renderEquipos();
  }
  if(document.getElementById('sec-jugadores') && document.getElementById('sec-jugadores').classList.contains('activa')) {
    renderJugadores();
  }
  if(document.getElementById('sec-estadios') && document.getElementById('sec-estadios').classList.contains('activa')) {
    renderEstadios();
  }
  if(document.getElementById('sec-partidos') && document.getElementById('sec-partidos').classList.contains('activa')) {
    renderPartidos();
  }
  if(document.getElementById('sec-simulador') && document.getElementById('sec-simulador').classList.contains('activa')) {
    renderBracket();
  }
  if(document.getElementById('sec-estadisticas') && document.getElementById('sec-estadisticas').classList.contains('activa')) {
    renderEstadisticas();
  }
}

// ── CRUD EQUIPOS ──
function renderEquipos() {
  const grid = document.getElementById('grid-equipos'); 
  if(!_equipos.length){ grid.innerHTML='<div class="loading" style="grid-column: 1 / -1;">Sin datos</div>'; return; }
  grid.innerHTML = _equipos.map(e => `
    <div class="equipo-card" onclick="verDetalleEquipo(${e.idEquipo||e.idequipo})">
      <div class="flag">${getFlagHtml(e.pais||e.nombre)}</div>
      <div class="name">${e.pais||e.nombre}</div>
    </div>
  `).join('');
}
function verDetalleEquipo(id) {
  const e = _equipos.find(x => (x.idEquipo||x.idequipo) === id);
  if (!e) return;
  const cont = document.getElementById('detalle-equipo-cont');
  cont.innerHTML = `
    <div class="flag">${getFlagHtml(e.pais||e.nombre)}</div>
    <div class="name">${e.pais||e.nombre}</div>
    <div class="info">Grupo ${e.grupo||'—'}</div>
    <div class="detalle-acciones">
      <button class="btn btn-azul" onclick='editarEquipo(${JSON.stringify(e).replace(/'/g,"&apos;")})'>Editar</button>
      <button class="btn btn-rojo" onclick="eliminarEquipo(${e.idEquipo||e.idequipo}, true)">Eliminar</button>
    </div>
  `;
  mostrar('equipo-detalle');
}
function abrirModalEquipo() { document.getElementById('modal-equipo-titulo').innerText='NUEVO EQUIPO'; document.getElementById('eq-id').value=''; document.getElementById('eq-pais').value=''; document.getElementById('eq-grupo').value=''; document.getElementById('eq-idgrupo').value=''; document.getElementById('modal-equipo').style.display='flex'; }
function cerrarModalEquipo() { document.getElementById('modal-equipo').style.display='none'; }
function editarEquipo(e) { document.getElementById('modal-equipo-titulo').innerText='EDITAR EQUIPO'; document.getElementById('eq-id').value=e.idEquipo||e.idequipo; document.getElementById('eq-pais').value=e.pais||e.nombre; document.getElementById('eq-grupo').value=e.grupo; document.getElementById('eq-idgrupo').value=e.idGrupo||''; document.getElementById('modal-equipo').style.display='flex'; }
async function guardarEquipo() { const id=document.getElementById('eq-id').value, data={ id_equipo:id?parseInt(id):0, pais:document.getElementById('eq-pais').value, grupo:document.getElementById('eq-grupo').value, id_grupo:parseInt(document.getElementById('eq-idgrupo').value)||0 }; try { await fetch(API+'/equipos', {method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalEquipo(); _equipos=await get('/equipos'); renderEquipos(); document.getElementById('cnt-equipos').textContent=_equipos.length; toast('Guardado'); mostrar('equipos'); } catch(e){} }
async function eliminarEquipo(id, fromDetalle = false) { if(confirm('¿Eliminar equipo?')){ await fetch(API+'/equipos', {method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_equipo:id})}); _equipos=await get('/equipos'); renderEquipos(); document.getElementById('cnt-equipos').textContent=_equipos.length; toast('Eliminado'); if(fromDetalle) mostrar('equipos'); } }

// ── CRUD JUGADORES ──
function renderJugadores(){ filtrarJugadores(); }
function filtrarJugadores(){
  const q=document.getElementById('buscar-jugador').value.toLowerCase(), cont=document.getElementById('jugadores-container');
  const arr=_jugadores.filter(j=>j.nombre.toLowerCase().includes(q)||(j.paisEquipo||'').toLowerCase().includes(q));
  if(!arr.length){cont.innerHTML='<div class="loading">Sin resultados</div>';return;}
  
  // Agrupar por país
  const grupos={};
  arr.forEach(j=>{ const p = j.paisEquipo||'Sin Equipo'; if(!grupos[p]) grupos[p]=[]; grupos[p].push(j); });
  
  // Renderizar
  cont.innerHTML=Object.keys(grupos).sort().map(p=>`<div class="pais-section"><div class="pais-header">${getFlagHtml(p,true)} ${p}</div><div class="jugadores-grid">${grupos[p].map(j=>`<div class="jugador-card"><div class="jugador-dorsal">${j.dorsal||'-'}</div><div class="jugador-info"><div class="jugador-nombre">${j.nombre}</div><div class="jugador-posicion">${j.posicion}</div></div><div class="jugador-acciones"><button class="btn-icon edit" title="Editar" onclick='editarJugador(${JSON.stringify(j).replace(/'/g,"&apos;")})'>Editar</button><button class="btn-icon delete" title="Eliminar" onclick="eliminarJugador(${j.idJugador||j.idjugador})">Eliminar</button></div></div>`).join('')}</div></div>`).join('');
}
function cargarSelectEquipos(){ document.getElementById('jug-idequipo').innerHTML='<option value="">-- Selecciona --</option>'+_equipos.map(e=>`<option value="${e.idEquipo||e.idequipo}">${e.pais||e.nombre}</option>`).join(''); }
function abrirModalJugador(){ cargarSelectEquipos(); document.getElementById('modal-jugador-titulo').innerText='NUEVO JUGADOR'; document.getElementById('jug-id').value=''; document.getElementById('jug-nombre').value=''; document.getElementById('jug-posicion').value='Delantero'; document.getElementById('jug-dorsal').value=''; document.getElementById('jug-idequipo').value=''; document.getElementById('modal-jugador').style.display='flex'; }
function cerrarModalJugador(){ document.getElementById('modal-jugador').style.display='none'; }
function editarJugador(j){ cargarSelectEquipos(); document.getElementById('modal-jugador-titulo').innerText='EDITAR JUGADOR'; document.getElementById('jug-id').value=j.idJugador||j.idjugador; document.getElementById('jug-nombre').value=j.nombre; document.getElementById('jug-posicion').value=j.posicion; document.getElementById('jug-dorsal').value=j.dorsal; document.getElementById('jug-idequipo').value=j.idEquipo||j.idequipo; document.getElementById('modal-jugador').style.display='flex'; }
async function guardarJugador(){ const id=document.getElementById('jug-id').value, data={ id_jugador:id?parseInt(id):0, nombre:document.getElementById('jug-nombre').value, posicion:document.getElementById('jug-posicion').value, dorsal:parseInt(document.getElementById('jug-dorsal').value)||0, id_equipo:parseInt(document.getElementById('jug-idequipo').value)||0 }; try { await fetch(API+'/jugadores',{method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalJugador(); _jugadores=await get('/jugadores'); renderJugadores(); document.getElementById('cnt-jugadores').textContent=_jugadores.length; toast('Guardado'); } catch(e){} }
async function eliminarJugador(id){ if(confirm('¿Eliminar jugador?')){ await fetch(API+'/jugadores',{method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_jugador:id})}); _jugadores=await get('/jugadores'); renderJugadores(); document.getElementById('cnt-jugadores').textContent=_jugadores.length; toast('Eliminado'); } }

// ── CRUD ESTADIOS ──
function renderEstadios() {
  const grid = document.getElementById('grid-estadios'); 
  if(!grid) return;
  if(!_estadios.length){ grid.innerHTML='<div class="loading" style="grid-column: 1 / -1;">Sin estadios registrados</div>'; return; }
  grid.innerHTML = _estadios.map((e,i) => `
    <div class="estadio-card">
      <div>
        <div class="estadio-nombre">${e.nombre}</div>
        <div class="estadio-ubicacion">${getFlagHtml(e.pais, true)} ${e.ciudad}, ${e.pais}</div>
        <div class="estadio-capacidad">Capacidad: ${(e.capacidad||0).toLocaleString()}</div>
      </div>
      <div class="estadio-acciones">
        <button class="btn btn-azul" style="padding:8px 16px;font-size:0.8rem;" onclick='editarEstadio(${JSON.stringify(e).replace(/'/g,"&apos;")})'>Editar</button>
        <button class="btn btn-rojo" style="padding:8px 16px;font-size:0.8rem;" onclick="eliminarEstadio(${e.idEstadio||e.idestadio})">Eliminar</button>
      </div>
    </div>
  `).join('');
}
function abrirModalEstadio() { document.getElementById('modal-estadio-titulo').innerText='NUEVO ESTADIO'; document.getElementById('est-id').value=''; document.getElementById('est-nombre').value=''; document.getElementById('est-ciudad').value=''; document.getElementById('est-pais').value=''; document.getElementById('est-capacidad').value=''; document.getElementById('modal-estadio').style.display='flex'; }
function cerrarModalEstadio() { document.getElementById('modal-estadio').style.display='none'; }
function editarEstadio(e) { document.getElementById('modal-estadio-titulo').innerText='EDITAR ESTADIO'; document.getElementById('est-id').value=e.idEstadio||e.idestadio; document.getElementById('est-nombre').value=e.nombre; document.getElementById('est-ciudad').value=e.ciudad; document.getElementById('est-pais').value=e.pais; document.getElementById('est-capacidad').value=e.capacidad||''; document.getElementById('modal-estadio').style.display='flex'; }
async function guardarEstadio() { const id=document.getElementById('est-id').value, data={ id_estadio:id?parseInt(id):0, nombre:document.getElementById('est-nombre').value, ciudad:document.getElementById('est-ciudad').value, pais:document.getElementById('est-pais').value, capacidad:parseInt(document.getElementById('est-capacidad').value)||0 }; try { await fetch(API+'/estadios', {method:id?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)}); cerrarModalEstadio(); _estadios=await get('/estadios'); renderEstadios(); document.getElementById('cnt-estadios').textContent=_estadios.length; toast('Estadio guardado'); } catch(e){} }
async function eliminarEstadio(id) { if(confirm('¿Eliminar estadio?')){ await fetch(API+'/estadios', {method:'DELETE', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id_estadio:id})}); _estadios=await get('/estadios'); renderEstadios(); document.getElementById('cnt-estadios').textContent=_estadios.length; toast('Eliminado'); } }

// ── VISTAS DE LECTURA ──
function renderEntrenadores() { 
  const tb = document.getElementById('tb-entrenadores'); 
  get('/entrenadores').then(d => { 
    tb.innerHTML = d.length ? d.map((e,i) => `<tr><td>${i+1}</td><td><strong>${e.nombre||'—'}</strong></td><td>${getFlagHtml(e.paisEquipo||'', true)}${e.paisEquipo||'—'}</td></tr>`).join('') : '<tr><td colspan="3">Sin datos</td></tr>'; 
  }); 
}
function renderGrupos() { 
  const cont = document.getElementById('grupos-container'); 
  if(!_posiciones.length) { cont.innerHTML='<div class="loading">Sin posiciones</div>'; return; } 
  const gs={}; 
  _posiciones.forEach(p => { 
    const grp = p.grupo || (_equipos.find(e=>e.idEquipo===p.idEquipo||e.id===p.idEquipo)||{}).grupo || '?'; 
    if(!gs[grp]) gs[grp]=[]; gs[grp].push(p); 
  }); 
  Object.values(gs).forEach(a => a.sort((x,y) => y.puntos-x.puntos || (y.gf-y.gc)-(x.gf-x.gc))); 
  cont.innerHTML = Object.entries(gs).sort().map(([g,pos]) => `<div class="grupo-card"><div class="grupo-header">GRUPO ${g}</div><table class="grupo-table"><thead><tr><th>País</th><th>PJ</th><th>PG</th><th>PE</th><th>PP</th><th>GF</th><th>GC</th><th>Pts</th></tr></thead><tbody>${pos.map((p,i)=>`<tr><td class="${i<2?'clasificado':''}">${getFlagHtml(p.pais, true)}${p.pais}</td><td>${p.pj}</td><td>${p.pg}</td><td>${p.pe}</td><td>${p.pp}</td><td>${p.gf}</td><td>${p.gc}</td><td><strong>${p.puntos}</strong></td></tr>`).join('')}</tbody></table></div>`).join(''); 
}
function renderPartidos() {
  const cont = document.getElementById('partidos-container');
  if (!cont) return;
  if (!_partidos.length) { cont.innerHTML = '<div class="loading">No hay partidos registrados</div>'; return; }
  
  // Agrupar los partidos por fase
  const porFase = {};
  _partidos.forEach(p => {
    const f = p.fase || 'Sin Fase';
    if (!porFase[f]) porFase[f] = [];
    porFase[f].push(p);
  });

  // Orden para mostrar: primero grupos, luego eliminatorias
  const ordenFases = ['FASE DE GRUPOS', 'DIECISEISAVOS DE FINAL', 'OCTAVOS DE FINAL', 'CUARTOS DE FINAL', 'SEMIFINAL', 'FINAL'];
  const fasesOrdenadas = Object.keys(porFase).sort((a,b) => {
    let ia = ordenFases.indexOf(a); let ib = ordenFases.indexOf(b);
    if (ia === -1) ia = 99; if (ib === -1) ib = 99;
    return ia - ib;
  });

  // Pintar el HTML
  cont.innerHTML = fasesOrdenadas.map(fase => `
    <div class="fase-section">
      <div class="fase-header">${fase}</div>
      <div class="partidos-grid">
        ${porFase[fase].map(p => `
          <div class="partido-card">
            <div class="partido-id">Partido #${p.idPartido}</div>
            <div class="partido-equipos">
              <div class="pe-equipo ${p.golesLocales > p.golesVisitantes ? 'ganador' : ''}"><span class="pe-bandera">${getFlagHtml(p.local, true)} ${p.local||'—'}</span><span class="pe-goles">${p.golesLocales}</span></div>
              <div class="pe-equipo ${p.golesVisitantes > p.golesLocales ? 'ganador' : ''}"><span class="pe-bandera">${getFlagHtml(p.visitante, true)} ${p.visitante||'—'}</span><span class="pe-goles">${p.golesVisitantes}</span></div>
            </div>
            <div class="partido-footer">${p.estadio||'—'}</div>
          </div>
        `).join('')}
      </div>
    </div>
  `).join('');
}

// ── SIMULADOR ──
async function simularFaseGrupos() {
  if (!_equipos.length) return toast('Carga equipos primero', true);
  
  // Pedimos confirmación al usuario, ya que es una acción destructiva.
  if (!confirm('Esto borrará todo el historial de torneos anteriores (partidos, goles, tarjetas). ¿Deseas empezar un nuevo mundial desde cero?')) {
    return;
  }

  document.getElementById('estado-grupos').textContent='Calculando en Java...';
  
  // Java ahora calcula y devuelve los 32 equipos clasificados
  const res = await fetch(API+'/simulador/grupos', {method:'POST'});
  const clasificados = await res.json();

  if (clasificados.ok === false) { // Manejo de error si no hay 48 equipos
    toast(clasificados.mensaje, true);
    document.getElementById('estado-grupos').textContent='Error';
    return;
  }

  // ¡SOLUCIÓN! Volvemos a cargar los datos que se borraron y actualizamos el Dashboard.
  [_partidos, _posiciones, _goles, _tarjetas] = await Promise.all([
    get('/partidos'), get('/posiciones'), get('/goles'), get('/tarjetas')
  ]);
  // Si estamos en el dashboard, actualizamos contadores
  if (document.getElementById('cnt-partidos')) document.getElementById('cnt-partidos').textContent = _partidos.length;
  
  const ag = {};
  _posiciones.forEach(p => {
    const grp = (_equipos.find(e => e.idEquipo === p.idEquipo || e.idequipo === p.idEquipo) || {}).grupo || '?';
    if(!ag[grp]) ag[grp] = [];
    ag[grp].push({ idEquipo: p.idEquipo, pais: p.pais, pts: p.puntos, pj: p.pj, gf: p.gf, gc: p.gc });
  });
  
  gruposSimulados = {};
  Object.entries(ag).forEach(([g, eqs]) => { gruposSimulados[g] = eqs.sort((a,b) => b.pts - a.pts || (b.gf - b.gc) - (a.gf - a.gc) || b.gf - a.gf); });
  
  gruposSimulado=true; document.getElementById('estado-grupos').textContent='¡Completado!';
  document.getElementById('grupos-sim-container').innerHTML = Object.entries(gruposSimulados).sort().map(([g,e]) => `<div class="grupo-sim-card"><div class="grupo-sim-header">GRUPO ${g}</div><table class="grupo-table"><thead><tr><th>País</th><th>Pts</th></tr></thead><tbody>${e.map((x,i)=>`<tr><td style="color:${i<2?'var(--verde)':(i===2?'var(--oro)':'gray')}">${getFlagHtml(x.pais, true)}${x.pais}</td><td>${x.pts}</td></tr>`).join('')}</tbody></table></div>`).join('');
  
  // CORRECCIÓN: Preparamos los datos para la siguiente fase, pero no cambiamos de pantalla.
  faseIdx=0; 
  rondas=[]; 
  campeón=null; 
  iniciarRonda0(clasificados); 
  renderBracket(); // Dejamos el bracket listo en la otra pestaña.
  
  document.getElementById('btn-ir-llaves').style.display = 'inline-block';
  
  toast('¡Fase de grupos completada! Haz clic en "Llaves de Eliminación" para continuar.');
}

function iniciarRonda0(clasificados) {
  let cl = clasificados || [];
  cl.sort(()=>Math.random()-0.5); // Barajamos los 32 clasificados
  rondas=[[]];
  for(let i=0; i<cl.length; i+=2) rondas[0].push({ idLocal: (cl[i]||{}).idEquipo||(cl[i]||{}).idequipo||0, local: (cl[i]||{}).pais||'—', idVisitante: (cl[i+1]||{}).idEquipo||(cl[i+1]||{}).idequipo||0, visitante: (cl[i+1]||{}).pais||'—', golesL: null, golesV: null, ganador: null });
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

async function simularTodo() { 
  toast('Botón no funcional con la nueva lógica. Simula por fases.', true); 
  /* La lógica anterior se rompió con el cambio a 48 equipos. Se requiere un refactor mayor. */ 
}
function resetSimulador(){ faseIdx=0;rondas=[];campeón=null;document.getElementById('fase-actual').textContent=FASES[0];renderBracket(); }
function resetTodo(){ 
  gruposSimulados={}; gruposSimulado=false; 
  document.getElementById('grupos-sim-container').innerHTML='<div class="loading">Presiona "Simular Fase de Grupos" para comenzar el torneo...</div>'; 
  document.getElementById('estado-grupos').textContent=''; 
  if(document.getElementById('btn-ir-llaves')) document.getElementById('btn-ir-llaves').style.display='none';
  resetSimulador(); 
  irAFase('grupos-sim'); 
}
function renderBracket() { 
  const bracketEl = document.getElementById('bracket');
  if(!bracketEl) return;
  if(!rondas.length) { bracketEl.innerHTML = ''; return; }
  
  let htmlLeft = '<div class="bracket-half left-half">';
  let htmlRight = '<div class="bracket-half right-half">';
  
  for(let i=0; i<4; i++) {
    if(i < rondas.length) {
      let r = rondas[i]; let half = r.length / 2;
      let renderRound = (matches) => `<div class="ronda"><div class="ronda-titulo">${FASES[i]||''}</div><div class="ronda-partidos">${matches.map(m=>`<div class="match-card"><div class="match-equipo ${m.ganador===m.local?'ganador':m.ganador?'perdedor':''}"><span>${getFlagHtml(m.local, true)}${m.local}</span>${m.golesL!==null?`<span class="gol-badge">${m.golesL}</span>`:''}</div><div class="match-equipo ${m.ganador===m.visitante?'ganador':m.ganador?'perdedor':''}"><span>${getFlagHtml(m.visitante, true)}${m.visitante}</span>${m.golesV!==null?`<span class="gol-badge">${m.golesV}</span>`:''}</div></div>`).join('')}</div></div>`;
      htmlLeft += renderRound(r.slice(0, half));
      htmlRight += renderRound(r.slice(half));
    }
  }
  htmlLeft += '</div>'; htmlRight += '</div>';
  
  let htmlCenter = '<div class="bracket-center">';
  if(rondas.length > 4) {
    let m = rondas[4][0];
    htmlCenter += `
      <div class="ronda">
        <div class="ronda-titulo">GRAN FINAL</div>
        <div class="ronda-partidos final-match">
          <div class="match-card final-card"><div class="match-equipo ${m.ganador===m.local?'ganador':m.ganador?'perdedor':''}"><span>${getFlagHtml(m.local, true)}${m.local}</span>${m.golesL!==null?`<span class="gol-badge">${m.golesL}</span>`:''}</div><div class="match-equipo ${m.ganador===m.visitante?'ganador':m.ganador?'perdedor':''}"><span>${getFlagHtml(m.visitante, true)}${m.visitante}</span>${m.golesV!==null?`<span class="gol-badge">${m.golesV}</span>`:''}</div></div>
        </div>
      </div>
    `;
    if(campeón) htmlCenter += `<div class="campeon-center">Campeón<br>${getFlagHtml(campeón)}<br>${campeón}</div>`;
  } else {
    htmlCenter += `<div class="ronda-titulo" style="opacity: 0.5;">GRAN FINAL</div><div class="final-placeholder">TBD</div>`;
  }
  htmlCenter += '</div>';
  bracketEl.innerHTML = htmlLeft + htmlCenter + htmlRight;
}
async function limpiarBD(){ 
  if(confirm('¿Borrar TODO el historial de partidos, goles y tarjetas?')){ 
    await fetch(API+'/limpiar',{method:'POST',body:'{}'}); 
    _partidos=[]; _posiciones=[]; _goles=[]; _tarjetas=[];
    if(document.getElementById('cnt-partidos')) document.getElementById('cnt-partidos').textContent='0';
    resetTodo(); 
    toast('Historial en blanco'); 
  } 
}

// ── ESTADÍSTICAS Y PREMIOS FINALES ──
async function renderEstadisticas(){
  _goles = await get('/goles'); _tarjetas = await get('/tarjetas'); _partidos = await get('/partidos');
  if(campeón){ document.getElementById('campeon-box').style.display='block'; document.getElementById('campeon-nombre').innerHTML=getFlagHtml(campeón)+campeón; }
  
  // Pichichi (Solo 1)
  const cg={}; _goles.forEach(g=>{const k=g.jugador||('#'+g.idJugador); cg[k]=(cg[k]||0)+1;});
  const goleadores = Object.entries(cg).sort((a,b)=>b[1]-a[1]);
  renderPodio('podio-goleadores', goleadores.slice(0,1), 'goles');

  // 🧤 MEJOR PORTERO (Cálculo desde Controlador Java) - Solo 1
  const porterosJava = await get('/reportes/porteros');
  let statsPorteros = porterosJava.map(p => [p.nombre, p.promedio.toFixed(2)]);
  
  // Ordenar de MENOR promedio a MAYOR
  statsPorteros.sort((a, b) => parseFloat(a[1]) - parseFloat(b[1]));
  renderPodio('podio-porteros', statsPorteros.slice(0, 1), 'goles x partido');

  // Tarjetas separadas en Amarillas y Rojas
  const cAm = {}; const cRo = {};
  _tarjetas.forEach(t => {
      const k = t.jugador || ('#'+t.idJugador);
      if (t.tipo.toLowerCase() === 'amarilla') cAm[k] = (cAm[k]||0) + 1;
      else cRo[k] = (cRo[k]||0) + 1;
  });
  renderPodio('podio-amarillas', Object.entries(cAm).sort((a,b)=>b[1]-a[1]), 'amarillas');
  renderPodio('podio-rojas', Object.entries(cRo).sort((a,b)=>b[1]-a[1]), 'rojas');
}

function renderPodio(id,data,ud){ document.getElementById(id).innerHTML=data.length?data.map(([n,v],i)=>`<div class="podio-item"><div style="font-size:1.2rem;width:30px;color:var(--azul);font-weight:bold;">${i+1}.</div><div style="flex:1">${id==='podio-paises'?getFlagHtml(n, true):''}${n}</div><div class="podio-val">${v} <span style="font-size:0.6rem;color:var(--gris-oscuro)">${ud}</span></div></div>`).join(''):'<div class="loading">Sin datos suficientes</div>'; }

window.addEventListener('DOMContentLoaded', cargarDashboard);
