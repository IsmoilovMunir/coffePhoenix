<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import logoLuxury from "../assets/logo/lgo.svg";

const navOpen = ref(false);
const isScrolled = ref(false);

function handleScroll() {
  isScrolled.value = window.scrollY > 8;
}

function toggleNav() {
  navOpen.value = !navOpen.value;
}

function closeNav() {
  navOpen.value = false;
}

onMounted(() => {
  handleScroll();
  window.addEventListener("scroll", handleScroll, { passive: true });
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<template>
  <header class="header header--luxury" :class="{ 'header--scrolled': isScrolled }" id="top">
    <div class="header__bar header__bar--luxury">
      <a class="logo logo--luxury" href="#top">
        <img
          class="logo__img logo__img--luxury"
          :src="logoLuxury"
          width="315"
          height="205"
          alt="Кофе Феникс"
          decoding="async"
        />
      </a>
      <button
        class="nav-toggle nav-toggle--luxury"
        type="button"
        :aria-expanded="navOpen"
        aria-controls="site-nav"
        @click="toggleNav"
      >
        <span class="nav-toggle__line"></span>
        <span class="nav-toggle__line"></span>
        <span class="visually-hidden">Открыть меню</span>
      </button>
      <nav
        class="nav nav--luxury"
        id="site-nav"
        :class="{ 'is-open': navOpen }"
        aria-label="Основное меню"
      >
        <ul class="nav__list nav__list--luxury">
          <li class="nav__item--current">
            <a href="#hero" @click="closeNav">Главная</a>
          </li>
          <li><a href="#story" @click="closeNav">О нас</a></li>
          <li><a href="#menu" @click="closeNav">Ассортимент</a></li>
          <li><a href="#contacts" @click="closeNav">Контакты</a></li>
          <li class="nav__extra"><a href="#cafes" @click="closeNav">Кофейни</a></li>
          <li class="nav__extra"><a href="#news" @click="closeNav">Новости</a></li>
          <li class="nav__extra"><a href="#franchise" @click="closeNav">Франшиза</a></li>
          <li class="nav__extra"><a href="#vacancies" @click="closeNav">Вакансии</a></li>
        </ul>
      </nav>
      <a class="header__phone header__phone--luxury" href="tel:+79856678777">+7 985 667 87 77</a>
    </div>
  </header>
</template>
