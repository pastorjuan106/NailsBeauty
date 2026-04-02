document.addEventListener("DOMContentLoaded", () => {
  const track = document.querySelector(".carousel-track");
  const prevBtn = document.querySelector(".prev-btn");
  const nextBtn = document.querySelector(".next-btn");
  const cards = document.querySelectorAll(".servicio-card");

  const totalCards = cards.length;
  const visibleCards = 3;
  let currentIndex = 0;

  function updateCarousel() {
    const moveX = currentIndex * (100 / visibleCards);
    track.style.transform = `translateX(-${moveX}%)`;
  }

  nextBtn.addEventListener("click", () => {
    if (currentIndex < totalCards - visibleCards) {
      currentIndex++;
      updateCarousel();
    }
  });

  prevBtn.addEventListener("click", () => {
    if (currentIndex > 0) {
      currentIndex--;
      updateCarousel();
    }
  });
});
