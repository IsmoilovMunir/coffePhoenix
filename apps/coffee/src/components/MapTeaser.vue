<template>
  <section class="map-teaser" id="map">
    <div class="map-teaser__map-wrap">
      <iframe
        class="map-teaser__frame"
        src="https://yandex.ru/map-widget/v1/?ll=37.874578%2C55.657304&z=17&pt=37.874578,55.657304,pm2rdm"
        title="Кофе Феникс на Яндекс Карте"
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade"
      ></iframe>
      <a
        class="map-teaser__marker-link"
        href="https://yandex.ru/maps/?rtext=~55.657304,37.874578&rtt=auto"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Построить маршрут до кофейни"
        title="Построить маршрут"
      >
        <img
          class="map-teaser__marker-logo"
          src="/src/assets/logo/lgo.svg"
          alt=""
          aria-hidden="true"
        />
      </a>
    </div>
  </section>
</template>
