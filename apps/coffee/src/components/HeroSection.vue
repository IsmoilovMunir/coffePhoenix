<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from "vue";
import FeatureCard from "./FeatureCard.vue";
import SocialBar from "./SocialBar.vue";

const drinks = [
  {
    id: "signature",
    label: "Авторский",
    src: "/fonts/coffe.png",
    alt: "Авторский напиток, Кофе Феникс",
  },
  {
    id: "latte",
    label: "Латте",
    src: "/fonts/pngwing.com.png",
    alt: "Латте, Кофе Феникс",
  },
  {
    id: "cappuccino",
    label: "Капучино",
    src: `/fonts/${encodeURIComponent("—Pngtree—delicious cappuccino coffee cup with_19991380.png")}`,
    alt: "Капучино, Кофе Феникс",
  },
  {
    id: "raf",
    label: "Раф",
    src: "/fonts/95934300.png",
    alt: "Раф, Кофе Феникс",
  },
];

const TILT_DEG = 45;
const TILT_MS = 420;

const idx = ref(0);
const pendingTarget = ref(null);
const tiltDeg = ref(0);
const instantTilt = ref(false);
const isAnimating = ref(false);
/** Запасной таймер, если transitionend не пришёл (WebKit / особые случаи) */
let tiltFallbackId = null;

const frontDrink = computed(() => drinks[idx.value]);

const cupTiltStyle = computed(() => {
  const t = `translateZ(0.1px) rotateY(${tiltDeg.value}deg)`;
  return {
    transform: t,
    WebkitTransform: t,
  };
});

const reducedMotionQuery = ref(null);

function syncReducedMotion() {
  return (
    typeof window !== "undefined" &&
    window.matchMedia("(prefers-reduced-motion: reduce)").matches
  );
}

const prefersReducedMotion = ref(false);

function onMotionPreferenceChange(e) {
  prefersReducedMotion.value = e.matches;
}

onMounted(() => {
  prefersReducedMotion.value = syncReducedMotion();
  reducedMotionQuery.value = window.matchMedia("(prefers-reduced-motion: reduce)");
  reducedMotionQuery.value.addEventListener("change", onMotionPreferenceChange);
});

onUnmounted(() => {
  reducedMotionQuery.value?.removeEventListener("change", onMotionPreferenceChange);
  if (tiltFallbackId !== null) clearTimeout(tiltFallbackId);
});

function clearTiltFallback() {
  if (tiltFallbackId !== null) {
    clearTimeout(tiltFallbackId);
    tiltFallbackId = null;
  }
}

function isTransformTransitionProp(name) {
  return name === "transform" || name === "-webkit-transform";
}

function finishTiltAfterAnimation() {
  if (pendingTarget.value === null) return;
  if (Math.abs(tiltDeg.value) !== TILT_DEG) return;

  const target = pendingTarget.value;
  clearTiltFallback();

  instantTilt.value = true;
  idx.value = target;
  pendingTarget.value = null;
  tiltDeg.value = 0;

  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      instantTilt.value = false;
      isAnimating.value = false;
    });
  });
}

async function goTo(target) {
  if (target === idx.value || isAnimating.value) return;
  if (target < 0 || target >= drinks.length) return;

  if (prefersReducedMotion.value) {
    idx.value = target;
    return;
  }

  const n = drinks.length;
  const forward = (target - idx.value + n) % n;
  const backward = (idx.value - target + n) % n;
  /** Короче по кругу «вперёд» → наклон в одну сторону, «назад» → в другую */
  const goForward = forward <= backward;

  clearTiltFallback();
  isAnimating.value = true;
  pendingTarget.value = target;
  await nextTick();
  await new Promise((r) => requestAnimationFrame(r));
  await new Promise((r) => requestAnimationFrame(r));
  tiltDeg.value = goForward ? -TILT_DEG : TILT_DEG;

  tiltFallbackId = window.setTimeout(() => {
    tiltFallbackId = null;
    finishTiltAfterAnimation();
  }, TILT_MS + 120);
}

function onTiltTransitionEnd(e) {
  if (!isTransformTransitionProp(e.propertyName)) return;
  finishTiltAfterAnimation();
}

function goNext() {
  goTo((idx.value + 1) % drinks.length);
}

function goPrev() {
  goTo((idx.value - 1 + drinks.length) % drinks.length);
}

let swipeStartX = 0;

function onSwipeStart(e) {
  const p = e.touches ? e.touches[0] : e;
  swipeStartX = p.clientX;
}

function onSwipeEnd(e) {
  const p = e.changedTouches ? e.changedTouches[0] : e;
  const dx = p.clientX - swipeStartX;
  if (Math.abs(dx) < 44) return;
  if (dx < 0) goNext();
  else goPrev();
}
</script>

<template>
  <section id="hero" class="hero-landing" aria-label="Главный экран">
    <div class="wrap hero-landing__inner">
      <div class="hero-landing__main">
        <div class="hero-landing__col hero-landing__col--left">
          <div class="hero-landing__intro">
            <h1 class="hero-landing__title">
              Искусство <span class="hero-landing__title-gold">кофе</span>
            </h1>
            <p class="hero-landing__lead">
              Тщательно подобранная смесь и обжарка с характером. Потому что хороший кофе никогда не
              спешит.
            </p>
            <a class="hero-landing__cta" href="#menu">Узнать больше</a>
          </div>
          <SocialBar class="hero-landing__social" />
        </div>

        <div class="hero-landing__cup-column">
          <!-- Десктоп: один статичный стакан -->
          <div class="hero-landing__cup-wrap hero-landing__cup-wrap--desktop">
            <div class="hero-landing__cup-glow" aria-hidden="true"></div>
            <img
              class="hero-landing__cup hero-landing__cup--desktop"
              :src="drinks[0].src"
              width="560"
              height="728"
              :alt="drinks[0].alt"
              decoding="async"
              fetchpriority="high"
            />
          </div>

          <!-- Мобильный: наклон ±45° и смена напитка, превью внизу -->
          <div
            class="hero-drink-carousel"
            role="region"
            aria-roledescription="карусель"
            aria-label="Напитки: листайте или выберите миниатюру"
          >
            <div class="hero-drink-carousel__arcs" aria-hidden="true">
              <svg
                class="hero-drink-carousel__arc hero-drink-carousel__arc--left"
                viewBox="0 0 48 120"
                fill="none"
              >
                <path
                  d="M44 8C18 28 8 52 8 80c0 18 8 32 28 38"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
              <svg
                class="hero-drink-carousel__arc hero-drink-carousel__arc--right"
                viewBox="0 0 48 120"
                fill="none"
              >
                <path
                  d="M4 8c26 20 36 44 36 72 0 18-8 32-28 38"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
            </div>

            <div
              class="hero-drink-carousel__flip-root"
              @pointerdown="onSwipeStart"
              @pointerup="onSwipeEnd"
            >
              <div class="hero-drink-carousel__glow" aria-hidden="true"></div>
              <div class="hero-drink-carousel__perspective">
                <div
                  class="hero-drink-carousel__flip-inner"
                  :class="{ 'hero-drink-carousel__flip-inner--instant': instantTilt }"
                  :style="cupTiltStyle"
                  @transitionend.self="onTiltTransitionEnd"
                >
                  <div class="hero-drink-carousel__cup-stage">
                    <img
                      class="hero-drink-carousel__cup-img"
                      :src="frontDrink.src"
                      width="560"
                      height="728"
                      :alt="frontDrink.alt"
                      decoding="async"
                    />
                  </div>
                </div>
              </div>
            </div>

            <p class="hero-drink-carousel__label" aria-live="polite">{{ frontDrink.label }}</p>

            <div class="hero-drink-carousel__thumbs" role="tablist" aria-label="Выбор напитка">
              <button
                v-for="(d, i) in drinks"
                :key="d.id"
                type="button"
                class="hero-drink-carousel__thumb"
                :class="{ 'hero-drink-carousel__thumb--active': i === idx }"
                role="tab"
                :aria-selected="i === idx"
                :tabindex="i === idx ? 0 : -1"
                @click="goTo(i)"
              >
                <span class="hero-drink-carousel__thumb-ring"></span>
                <img :src="d.src" width="80" height="80" alt="" decoding="async" />
                <span class="visually-hidden">{{ d.label }}</span>
              </button>
            </div>

            <p class="hero-drink-carousel__tagline">Возможно, лучший кофе</p>
          </div>
        </div>

        <div class="hero-landing__col hero-landing__col--right">
          <p class="hero-landing__aside">
            <span class="hero-landing__aside-brand">Кофе Феникс</span>
            создан для тех, кто ценит глубину вкуса, честную обжарку и спокойную силу утренней чашки —
            без компромиссов и суеты.
          </p>
          <div class="hero-landing__cards">
            <FeatureCard
              variant="bean"
              title="Наши бленды"
              text="Эспрессо-смеси и альтернатива — от классики до авторских профилей."
            />
            <FeatureCard
              variant="cup"
              title="Авторские напитки"
              text="Классика и сезонные рецепты — баланс сладости, кислоты и текстуры."
            />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
