<script setup>
import { inject, ref } from "vue";
import { useCountUp } from "../composables/useCountUp";

const countersActive = inject("countersActive", ref(false));

const cups = useCountUp(countersActive, 5120, 1600);
const checks = useCountUp(countersActive, 1230, 1600);
const cafes = useCountUp(countersActive, 1, 1600);
const graduates = useCountUp(countersActive, 6, 1600);
</script>

<template>
  <section class="stats" id="stats">
    <div class="wrap">
      <p class="section-sub center">Статистика</p>
      <p class="lead center">
        Сеть создана для тех, кто ценит честный продукт и стабильный сервис в каждой чашке.
      </p>
      <div class="stats__grid">
        <article class="stat-card">
          <p class="stat-card__value"><span>{{ cups }}</span></p>
          <p class="stat-card__label">Среднее число чашек в месяц в кофейне</p>
        </article>
        <article class="stat-card">
          <p class="stat-card__value"><span>{{ checks }}</span></p>
          <p class="stat-card__label">Среднее число чеков в месяц в кофейне</p>
        </article>
        <article class="stat-card">
          <p class="stat-card__value"><span>{{ cafes }} +</span></p>
          <p class="stat-card__label">Количество кофеен в сети</p>
        </article>
        <article class="stat-card">
          <p class="stat-card__value"><span>{{ graduates }}</span></p>
          <p class="stat-card__label">Выпускников школы бариста</p>
        </article>
      </div>
    </div>
  </section>
</template>
