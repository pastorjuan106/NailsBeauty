document.addEventListener('DOMContentLoaded', function() {
	const serviceColors = {
	  "Manicure": "#ff6f91",        
	  "Pedicure": "#ffb347",        
	  "Uñas Acrílicas": "#6a0dad",  
	  "Nail Art": "#1e90ff"         
	};

  const legendEl = document.getElementById('calendar-legend');
  for (const [service, color] of Object.entries(serviceColors)) {
    const item = document.createElement('div');
    item.className = 'legend-item';
    item.innerHTML = `<div class="legend-color" style="background-color:${color}"></div>${service}`;
    legendEl.appendChild(item);
  }

  var calendarEl = document.getElementById('calendar');
  var calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: 'dayGridMonth',
	headerToolbar: {
	  left: 'prev,next today',
	  center: 'title',
	  right: 'dayGridDay,timeGridWeek,dayGridMonth'
	},
	buttonText: {
	  today: 'Hoy',
	  day: 'Día',
	  week: 'Semana',
	  month: 'Mes'
	},
	locale: 'es',
    events: '/admin/agenda/eventos', 
    eventColor: '#d63384', 
    eventTextColor: '#fff',
    editable: false,
    height: 'auto',
    navLinks: true,
    eventDidMount: function(info) {
      const title = info.event.title.toLowerCase();
      for (const [service, color] of Object.entries(serviceColors)) {
        if (title.includes(service.toLowerCase())) {
          info.el.style.backgroundColor = color;
          break; 
        }
      }
    },
    eventClick: function(info) {
      alert(info.event.title + "\nEstado: " + info.event.extendedProps.estado);
    }
  });
  calendar.render();
});
