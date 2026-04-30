<script setup>
import { inject, ref } from "vue";
import { useCountUp } from "../composables/useCountUp";

const countersActive = inject("countersActive", ref(false));
const cafeDisplay = useCountUp(countersActive, 1, 1200);
</script>

<template>
  <section class="cafes-strip" id="cafes">
    <div class="wrap cafes-strip__inner">
      <div>
        <h2 class="cafes-strip__title">Наши кофейни</h2>
        <p class="cafes-strip__quote">«Хороший кофе должен стоить разумных денег»</p>
        <p class="cafes-strip__stat"><span>{{ cafeDisplay }} +</span> точек</p>
      </div>
      <a class="btn btn--solid" href="#map">Посмотреть на карте</a>
    </div>
  </section>
</template>
