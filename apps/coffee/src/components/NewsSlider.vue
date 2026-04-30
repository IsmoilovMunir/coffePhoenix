<script setup>
import { ref, computed } from "vue";

const items = [
  {
    date: "2026-04-21",
    label: "21 апреля 2026",
    title: "Спасибо за наше общее чудо",
    text: "Празднуем вместе с гостями и командой.",
  },
  {
    date: "2026-04-15",
    label: "15 апреля 2026",
    title: "Выставка и дегустации",
    text: "Встречаемся на индустриальном событии этой весной.",
  },
  {
    date: "2026-04-05",
    label: "05 апреля 2026",
    title: "Coffeephenix × город",
    text: "Совместный проект с партнёрами мобильности.",
  },
];

const index = ref(0);

const trackStyle = computed(() => ({
  transform: `translateX(${-index.value * 100}%)`,
}));

const counterLabel = computed(() => `${index.value + 1} / ${items.length}`);

function prev() {
  index.value = (index.value - 1 + items.length) % items.length;
}

function next() {
  index.value = (index.value + 1) % items.length;
}

const touchStartX = ref(0);

function onTouchStart(e) {
  touchStartX.value = e.changedTouches[0].screenX;
}

function onTouchEnd(e) {
  const dx = e.changedTouches[0].screenX - touchStartX.value;
  if (dx > 50) prev();
  else if (dx < -50) next();
}
</script>

<template>
  <section class="news" id="news">
    <div class="wrap">
      <div class="section-head">
        <h2 class="section-title">Новости</h2>
        <p class="section-sub">События, коллаборации, акции</p>
      </div>
      <div class="news-slider">
        <div class="news-slider__viewport">
          <ul
            class="news-slider__track"
            :style="trackStyle"
            @touchstart.passive="onTouchStart"
            @touchend.passive="onTouchEnd"
          >
            <li v-for="item in items" :key="item.date" class="news-card">
              <time :datetime="item.date">{{ item.label }}</time>
              <h3>{{ item.title }}</h3>
              <p>{{ item.text }}</p>
            </li>
          </ul>
        </div>
        <div class="news-slider__controls">
          <button class="icon-btn" type="button" aria-label="Предыдущая новость" @click="prev">
            ‹
          </button>
          <span class="news-slider__counter">{{ counterLabel }}</span>
          <button class="icon-btn" type="button" aria-label="Следующая новость" @click="next">
            ›
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
