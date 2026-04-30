<script setup>
import { computed, onMounted, onUnmounted, ref } from "vue";
import interiorOneImg from "../assets/img/455A0077.jpg";
import interiorTwoImg from "../assets/img/455A0085.jpg";
import interiorThreeImg from "../assets/img/455A0099.jpg";
import interiorFourImg from "../assets/img/455A0137.jpg";

const billboardSlides = [
  {
    title: "Актуальность рынка",
    lead: "Рынок Москвы 2026 года - это рынок эмоциональной привязанности.",
    text: "В условиях инфляции и налогового давления выживают проекты, которые предлагают не просто продукт, а интеграцию в образ жизни клиента и стабильное качество, защищенное сильным брендом группы.",
    image: interiorOneImg,
  },
  {
    title: "Ваша выгода",
    lead: "Стратегически верное решение в текущих рыночных условиях.",
    text: "«Кофе Феникс» как часть группы дает вход в нишу с конкурентным средним чеком и лояльной аудиторией. Вы получаете понятный продукт, сильный визуальный образ и эмоциональную связь с гостем.",
    image: interiorTwoImg,
  },
  {
    title: "Атмосфера бренда",
    lead: "Интерьер и подача работают как продолжение меню.",
    text: "Сильная визуальная среда превращает визит в привычку: гостю комфортно возвращаться, а бренд остается узнаваемым в офлайн-точке и в контенте соцсетей.",
    image: interiorThreeImg,
  },
  {
    title: "Рост лояльности",
    lead: "Стабильный сервис усиливает повторные визиты.",
    text: "Когда качество напитка и атмосфера совпадают, клиент быстрее становится постоянным. Это повышает частоту покупок и удержание без агрессивных скидок.",
    image: interiorFourImg,
  },
];

const slideIdx = ref(0);
let autoTimer = null;

const currentSlide = computed(() => billboardSlides[slideIdx.value]);

function nextSlide() {
  slideIdx.value = (slideIdx.value + 1) % billboardSlides.length;
}

function prevSlide() {
  slideIdx.value = (slideIdx.value - 1 + billboardSlides.length) % billboardSlides.length;
}

function setSlide(index) {
  slideIdx.value = index;
}

function startAutoPlay() {
  autoTimer = window.setInterval(() => {
    nextSlide();
  }, 4500);
}

function stopAutoPlay() {
  if (autoTimer) {
    clearInterval(autoTimer);
    autoTimer = null;
  }
}

onMounted(() => {
  startAutoPlay();
});

onUnmounted(() => {
  stopAutoPlay();
});
</script>

<template>
  <section class="timeline" aria-label="Актуальность рынка">
    <div class="wrap timeline__inner">
      <article
        class="timeline__billboard"
        @mouseenter="stopAutoPlay"
        @mouseleave="startAutoPlay"
      >
        <img
          class="timeline__billboard-img"
          :src="currentSlide.image"
          :alt="currentSlide.title"
          loading="lazy"
          decoding="async"
        />
        <div class="timeline__overlay"></div>
        <div class="timeline__content">
          <p class="timeline__kicker">Билборд</p>
          <h2 class="timeline__title">{{ currentSlide.title }}</h2>
          <p class="timeline__lead">{{ currentSlide.lead }}</p>
          <p class="timeline__text timeline__text--last">{{ currentSlide.text }}</p>
        </div>
        <div class="timeline__slider-controls timeline__slider-controls--billboard">
          <button type="button" class="timeline__ctrl" @click="prevSlide">‹</button>
          <span>{{ slideIdx + 1 }} / {{ billboardSlides.length }}</span>
          <button type="button" class="timeline__ctrl" @click="nextSlide">›</button>
        </div>
      </article>
      <div class="timeline__dots" role="tablist" aria-label="Слайды билборда">
        <button
          v-for="(_, index) in billboardSlides"
          :key="index"
          type="button"
          class="timeline__dot"
          :class="{ 'timeline__dot--active': slideIdx === index }"
          :aria-label="`Перейти к слайду ${index + 1}`"
          :aria-selected="slideIdx === index"
          @click="setSlide(index)"
        ></button>
      </div>
    </div>
  </section>
</template>
