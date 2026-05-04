<script setup>
import { ref, computed } from "vue";
import espressoImg from "../assets/img/espresso.png";
import americanoImg from "../assets/img/americano.png";
import cappuccinoImg from "../assets/img/cappuccino.png";
import latteImg from "../assets/img/latte.png";
import flatWhiteImg from "../assets/img/flat-white.png";
import rafImg from "../assets/img/raf.png";
import bumbelImg from "../assets/img/bumbel.png";
import glyasImg from "../assets/img/glyas.png";
import kakaoImg from "../assets/img/kakao.png";
import matchaImg from "../assets/img/matcha.png";
import chayListovImg from "../assets/img/chay-listov.png";
import chayShafranImg from "../assets/img/chay-shafran.png";
import hotChocolateImg from "../assets/img/goryachi-shokolad.png";
import kofeFeniksImg from "../assets/img/kofe-feniks.png";
import rafHalvaImg from "../assets/img/raf-xalva.png";
import latteBauntImg from "../assets/img/latte-baunt.png";
import latteLukumImg from "../assets/img/lake-lukum.png";
import yagodnayaMatchaImg from "../assets/img/yagadnayz-match.png";
import mangoMatchImg from "../assets/img/mango-match.png";
import shirChayImg from "../assets/img/shirchay.png";
import chayKlukaviyImg from "../assets/img/chay-klukaviy.png";
import shokaladImg from "../assets/img/shokalad.png";
import klubnichniyImg from "../assets/img/klubnichniy.png";
import limonadLavandaImg from "../assets/img/limonad lavanda.png";
import limonadEzhevikaImg from "../assets/img/limonad ezhevika.png";
import citrusoviyImg from "../assets/img/citrusoviy.png";
import maxitoImg from "../assets/img/maxito.png";
import silivochniyImg from "../assets/img/silivochniy.png";

const activeTab = ref("coffee");

const coffeeDrinks = [
  {
    area: "a",
    name: "Эспрессо",
    sizes: [
      { volume: "200 мл", price: 230 },
    ],
    img: espressoImg,
  },
  {
    area: "b",
    name: "Американо",
    sizes: [
      { volume: "200 мл", price: 210 },
      { volume: "300 мл", price: 230 },
      { volume: "400 мл", price: 260 },
    ],
    img: americanoImg,
  },
  {
    area: "c",
    name: "Капучино",
    sizes: [
      { volume: "200 мл", price: 290 },
      { volume: "300 мл", price: 330 },
      { volume: "400 мл", price: 370 },
    ],
    img: cappuccinoImg,
  },
  {
    area: "d",
    name: "Латте",
    sizes: [
      { volume: "200 мл", price: 290 },
      { volume: "300 мл", price: 330 },
      { volume: "400 мл", price: 370 },
    ],
    img: latteImg,
  },
  {
    area: "e",
    name: "Флет уайт",
    sizes: [
      { volume: "400 мл", price: 340 },
    ],
    img: flatWhiteImg,
  },
  {
    area: "f",
    name: "Раф",
    featured: true,
    sizes: [
      { volume: "200 мл", price: 330 },
      { volume: "300 мл", price: 360 },
      { volume: "400 мл", price: 390 },
    ],
    img: rafImg,
  },
  {
    area: "g",
    name: "Бамбл",
    sizes: [{ volume: "300 мл", price: 390 }],
    img: bumbelImg,
  },
  {
    area: "glace",
    name: "Глясе",
    sizes: [{ volume: "300 мл", price: 350 }],
    img: glyasImg,
  },
];

const nonCoffeeDrinks = [
  {
    area: "a",
    name: "Какао",
    sizes: [
      { volume: "300 мл", price: 220 },
      { volume: "400 мл", price: 250 },
    ],
    img: kakaoImg,
  },
  {
    area: "b",
    name: "Матча",
    sizes: [
      { volume: "200 мл", price: 220 },
      { volume: "300 мл", price: 240 },
      { volume: "400 мл", price: 260 },
    ],
    img: matchaImg,
  },
  {
    area: "c",
    name: "Чай листовой",
    sizes: [{ volume: "400 мл", price: 210 }],
    img: chayListovImg,
  },
  {
    area: "d",
    name: "Чай шафран / кардамон",
    sizes: [{ volume: "400 мл", price: 250 }],
    img: chayShafranImg,
  },
  {
    area: "e",
    name: "Горячий шоколад",
    sizes: [{ volume: "300 мл", price: 270 }],
    img: hotChocolateImg,
  },
];

const signatureCoffee = [
  {
    area: "a",
    name: "Кофе «Феникс»",
    sizes: [{ volume: "400 мл", price: 430 }],
    img: kofeFeniksImg,
  },
  {
    area: "b",
    name: "Раф халва",
    sizes: [
      { volume: "300 мл", price: 390 },
      { volume: "400 мл", price: 420 },
    ],
    img: rafHalvaImg,
  },
  {
    area: "c",
    name: "Латте «Баунти»",
    sizes: [{ volume: "400 мл", price: 370 }],
    img: latteBauntImg,
  },
  {
    area: "d",
    name: "Латте «Лукум»",
    sizes: [{ volume: "400 мл", price: 290 }],
    img: latteLukumImg,
  },
  {
    area: "e",
    name: "Ягодная матча",
    sizes: [{ volume: "300 мл", price: 270 }],
    img: yagodnayaMatchaImg,
  },
  {
    area: "f",
    name: "Манго матча",
    sizes: [{ volume: "300 мл", price: 270 }],
    img: mangoMatchImg,
  },
  {
    area: "g",
    name: "Шир чай",
    sizes: [{ volume: "300 мл", price: 250 }],
    img: shirChayImg,
  },
  {
    area: "h",
    name: "Чай клюква / можжевельник",
    sizes: [{ volume: "300 мл", price: 250 }],
    img: chayKlukaviyImg,
  },
];

const lemonadeShakes = [
  {
    area: "a",
    name: "Шоколадный милкшейк",
    sizes: [{ volume: "400 мл", price: 409 }],
    img: shokaladImg,
  },
  {
    area: "b",
    name: "Клубничный милкшейк",
    sizes: [{ volume: "400 мл", price: 409 }],
    img: klubnichniyImg,
  },
  {
    area: "c",
    name: "Лимонад лаванда / тоник",
    sizes: [{ volume: "400 мл", price: 310 }],
    img: limonadLavandaImg,
  },
  {
    area: "d",
    name: "Лимонад ежевика / мята",
    sizes: [{ volume: "400 мл", price: 310 }],
    img: limonadEzhevikaImg,
  },
  {
    area: "e",
    name: "Цитрусовый лимонад",
    sizes: [{ volume: "400 мл", price: 310 }],
    img: citrusoviyImg,
  },
  {
    area: "f",
    name: "Мохито",
    sizes: [{ volume: "400 мл", price: 290 }],
    img: maxitoImg,
  },
  {
    area: "g",
    name: "Сливочное пиво",
    sizes: [{ volume: "400 мл", price: 430 }],
    img: silivochniyImg,
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
  if (activeTab.value === "nonCoffee") return nonCoffeeDrinks;
  if (activeTab.value === "lemonades") return lemonadeShakes;
  return coffeeDrinks;
});
</script>

<template>
  <section
    id="menu"
    class="menu-section"
    :class="{
      'menu-section--signature': activeTab === 'signature',
      'menu-section--coffee': activeTab === 'coffee',
      'menu-section--lemonades': activeTab === 'lemonades',
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
            Авторское
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
          <button
            type="button"
            class="menu-section__tab"
            role="tab"
            :aria-selected="activeTab === 'lemonades'"
            @click="activeTab = 'lemonades'"
          >
            Лимонады / шейки
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
              ? 'Авторское'
              : activeTab === 'nonCoffee'
                ? 'Не кофе'
                : 'Лимонады / шейки'
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
