document.addEventListener("DOMContentLoaded", () => {
  const fechaInput = document.getElementById("fecha");
  const horarioSelect = document.getElementById("horario");

  fechaInput.addEventListener("change", () => {
    horarioSelect.innerHTML = '<option value="" disabled selected>Selecciona un horario</option>';

    fetch(`/reservas/horarios/disponibles?fecha=${fechaInput.value}`)
      .then(res => res.json())
      .then(horarios => {
        horarios.forEach(h => {
          const option = document.createElement("option");
          option.value = h.id;
          option.textContent = `${h.horaInicio} - ${h.horaFin}`;
          if (h.ocupado) {
            option.disabled = true;
            option.style.color = "gray";
            option.textContent += " (Ocupado)";
          }
          horarioSelect.appendChild(option);
        });
      })
      .catch(err => console.error("Error al cargar horarios:", err));
  });
});

$(document).ready(function() {
   $('select.form-select').select2({
     width: '100%',
     placeholder: 'Seleccionar...',
     allowClear: true
   });
 });