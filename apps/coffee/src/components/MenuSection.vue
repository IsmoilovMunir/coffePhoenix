<script setup>
import { ref, computed } from "vue";
import espressoImg from "../assets/img/espresso.png";
import americanoImg from "../assets/img/americano.png";
import cappuccinoImg from "../assets/img/cappuccino.png";
import latteImg from "../assets/img/latte.png";
import flatWhiteImg from "../assets/img/flat-white.png";
import rafImg from "../assets/img/raf.png";
import mocaccinoImg from "../assets/img/mocaccino.png";
import hotChocolateImg from "../assets/img/mocaccino.png";
import seaBuckthornImg from "../assets/img/sea-buckthorn.png";
import raspberryMulledImg from "../assets/img/raspberry-mulled.png";
import lingonberryJasmineTeaImg from "../assets/img/lingonberry-jasmine-tea.png";
import berryIceTeaImg from "../assets/img/berry-ice-tea.png";
import classicBerryTeaImg from "../assets/img/classic-berry-tea.png";
import berryCardamomImg from "../assets/img/berry-cardamom.png";
import jasmineRafImg from "../assets/img/jasmine-raf.png";
import mapleRafImg from "../assets/img/maple-raf.png";
import meltingLatteImg from "../assets/img/melting-latte.png";
import berryMintMocaccinoImg from "../assets/img/berry-mint-mocaccino.png";

const activeTab = ref("coffee");

const coffeeDrinks = [
  {
    area: "a",
    name: "Эспрессо",
    sizes: [
      { volume: "200 мл", price: 190 },
    ],
    img: espressoImg,
  },
  {
    area: "b",
    name: "Американо",
    sizes: [
      { volume: "200 мл", price: 190 },
      { volume: "300 мл", price: 210 },
      { volume: "400 мл", price: 230 },
    ],
    img: americanoImg,
  },
  {
    area: "c",
    name: "Капучино",
    sizes: [
      { volume: "200 мл", price: 240 },
      { volume: "300 мл", price: 260 },
      { volume: "400 мл", price: 310 },
    ],
    img: cappuccinoImg,
  },
  {
    area: "d",
    name: "Латте",
    sizes: [
      { volume: "200 мл", price: 240 },
      { volume: "300 мл", price: 260 },
      { volume: "400 мл", price: 310 },
    ],
    img: latteImg,
  },
  {
    area: "e",
    name: "Флет уайт",
    sizes: [
      { volume: "400 мл", price: 310 },
    ],
    img: flatWhiteImg,
  },
  {
    area: "f",
    name: "Раф",
    featured: true,
    sizes: [
      { volume: "200 мл", price: 300 },
      { volume: "300 мл", price: 330 },
      { volume: "400 мл", price: 360 },
    ],
    img: rafImg,
  },
  {
    area: "g",
    name: "Моккачино",
    sizes: [
      { volume: "200 мл", price: 260 },
      { volume: "300 мл", price: 290 },
      { volume: "400 мл", price: 330 },
    ],
    img: mocaccinoImg,
  },
];

const nonCoffeeDrinks = [
  {
    area: "a",
    name: "Какао",
    sizes: [{ volume: "200 мл", price: 190 }],
    img: "https://images.unsplash.com/photo-1517578239113-b03992dcdd25?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "b",
    name: "Матча",
    sizes: [
      { volume: "200 мл", price: 190 },
      { volume: "300 мл", price: 210 },
      { volume: "400 мл", price: 230 },
    ],
    img: "https://images.unsplash.com/photo-1515823662972-da6a2e4d3002?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "c",
    name: "Чай листовой",
    sizes: [
      { volume: "200 мл", price: 140 },
      { volume: "300 мл", price: 170 },
      { volume: "400 мл", price: 190 },
    ],
    img: "https://images.unsplash.com/photo-1523920290228-4f321a939b4c?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "d",
    name: "Горячий шоколад",
    sizes: [{ volume: "300 мл", price: 210 }],
    img: hotChocolateImg,
  },
];

const signatureCoffee = [
  {
    area: "a",
    name: "Облепиха / лаванда",
    sizes: [{ volume: "400 мл", price: 280 }],
    img: seaBuckthornImg,
  },
  {
    area: "b",
    name: "Малиновый глинтвейн",
    sizes: [{ volume: "400 мл", price: 280 }],
    img: raspberryMulledImg,
  },
  {
    area: "c",
    name: "Чай «брусника-жасмин»",
    sizes: [{ volume: "400 мл", price: 280 }],
    img: lingonberryJasmineTeaImg,
  },
  {
    area: "d",
    name: "Ягодный айс-ти с ромашкой",
    sizes: [{ volume: "400 мл", price: 280 }],
    img: berryIceTeaImg,
  },
  {
    area: "e",
    name: "Ягодный чай классический",
    sizes: [{ volume: "400 мл", price: 190 }],
    img: classicBerryTeaImg,
  },
  {
    area: "f",
    name: "Ягодный с кардамоном",
    sizes: [{ volume: "400 мл", price: 280 }],
    img: berryCardamomImg,
  },
  {
    area: "g",
    name: "Раф жасминовый",
    sizes: [{ volume: "400 мл", price: 370 }],
    img: jasmineRafImg,
  },
  {
    area: "h",
    name: "Кленовый раф",
    sizes: [{ volume: "400 мл", price: 370 }],
    img: mapleRafImg,
  },
  {
    area: "i",
    name: "Латте «Тающий пломбир»",
    sizes: [{ volume: "400 мл", price: 370 }],
    img: meltingLatteImg,
  },
  {
    area: "j",
    name: "Моккачино ягодно-мятный",
    sizes: [{ volume: "400 мл", price: 370 }],
    img: berryMintMocaccinoImg,
  },
];

const kitchen = [
  {
    area: "a",
    name: "Круассан классика",
    price: 180,
    volume: "120 г",
    img: "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "b",
    name: "Чизкейк",
    price: 290,
    volume: "160 г",
    img: "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "c",
    name: "Тост с авокадо",
    price: 420,
    volume: "220 г",
    img: "https://images.unsplash.com/photo-1541519227354-08fa5d50c44d?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "d",
    name: "Боул с лососем",
    price: 560,
    volume: "280 г",
    img: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "e",
    name: "Сырники",
    price: 240,
    volume: "180 г",
    img: "https://images.unsplash.com/photo-1562376558-0d899f2515e3?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "f",
    name: "Тарталетка лимон",
    price: 220,
    volume: "140 г",
    img: "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "g",
    name: "Сэндвич куриный",
    price: 380,
    volume: "260 г",
    img: "https://images.unsplash.com/photo-1528735602780-2552d46df56d?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "h",
    name: "Овсяная каша",
    price: 210,
    volume: "250 г",
    img: "https://images.unsplash.com/photo-1517673409368-ab0b450503fe?auto=format&fit=crop&w=600&q=80",
  },
  {
    area: "feat",
    name: "Завтрак Феникс",
    price: 640,
    volume: "420 г",
    featured: true,
    img: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=900&q=80",
  },
];

const currentItems = computed(() => {
  if (activeTab.value === "coffee") return coffeeDrinks;
  if (activeTab.value === "signature") return signatureCoffee;
  return nonCoffeeDrinks;
});
</script>

<template>
  <section
    id="menu"
    class="menu-section"
    :class="{
      'menu-section--signature': activeTab === 'signature',
      'menu-section--coffee': activeTab === 'coffee',
    }"
    aria-labelledby="menu-heading"
  >
    <div class="wrap menu-section__inner">
      <header class="menu-section__head">
        <div class="menu-section__title-wrap">
          <h2 id="menu-heading" class="menu-section__brand">Кофе Феникс</h2>
          <p class="menu-section__script" aria-hidden="true">меню</p>
        </div>

        <div class="menu-section__tabs" role="tablist" aria-label="Раздел меню">
          <button
            type="button"
            class="menu-section__tab"
            role="tab"
            :aria-selected="activeTab === 'coffee'"
            @click="activeTab = 'coffee'"
          >
            Кофе
          </button>
          <button
            type="button"
            class="menu-section__tab"
            role="tab"
            :aria-selected="activeTab === 'signature'"
            @click="activeTab = 'signature'"
          >
            Авторское 400 мл
          </button>
          <button
            type="button"
            class="menu-section__tab"
            role="tab"
            :aria-selected="activeTab === 'nonCoffee'"
            @click="activeTab = 'nonCoffee'"
          >
            Не кофе
          </button>
        </div>

        <p class="menu-section__addons" aria-label="Общие доплаты">
          <span>Сироп +30 ₽</span>
          <span>Альтернативное молоко +70 ₽</span>
        </p>
      </header>

      <div
        class="menu-board"
        role="tabpanel"
        :aria-label="
          activeTab === 'coffee'
            ? 'Кофе'
            : activeTab === 'signature'
              ? 'Авторское 400 мл'
              : 'Не кофе'
        "
      >
        <article
          v-for="item in currentItems"
          :key="`${activeTab}-${item.area}`"
          class="menu-card"
          :class="[
            `menu-card--${item.area}`,
            { 'menu-card--featured': item.featured },
          ]"
        >
          <div class="menu-card__wave" aria-hidden="true">
            <svg viewBox="0 0 400 120" preserveAspectRatio="none" focusable="false">
              <path
                fill="rgba(237,232,226,0.06)"
                d="M0,80 Q100,40 200,70 T400,55 L400,120 L0,120 Z"
              />
              <path
                fill="rgba(8,51,50,0.35)"
                d="M0,95 Q120,55 240,85 T400,70 L400,120 L0,120 Z"
              />
            </svg>
          </div>

          <h3 class="menu-card__name">
            <span class="menu-card__name-inner">(( {{ item.name }} ))</span>
          </h3>

          <div class="menu-card__visual">
            <div class="menu-card__img-shell">
              <img
                class="menu-card__img"
                :src="item.img"
                :alt="item.name"
                loading="lazy"
                decoding="async"
                width="600"
                height="600"
              />
            </div>
          </div>

          <div v-if="item.sizes" class="menu-card__sizes" aria-label="Размеры и цены">
            <div
              v-for="size in item.sizes"
              :key="`${item.area}-${size.volume}`"
              class="menu-card__size-row"
            >
              <span class="menu-card__vol">{{ size.volume }}</span>
              <span class="menu-card__price">{{ size.price }} ₽</span>
            </div>
          </div>

          <div v-else class="menu-card__meta">
            <span class="menu-card__price">{{ item.price }} ₽</span>
            <span class="menu-card__vol">{{ item.volume }}</span>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>
