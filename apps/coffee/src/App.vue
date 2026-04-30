<script setup>
import { ref, provide, onMounted, onUnmounted } from "vue";
import AppHeader from "./components/AppHeader.vue";
import HeroSection from "./components/HeroSection.vue";
import BrandStorySection from "./components/BrandStorySection.vue";
import MenuSection from "./components/MenuSection.vue";
import TimelineSection from "./components/TimelineSection.vue";
import CafesStrip from "./components/CafesStrip.vue";
import FranchiseHero from "./components/FranchiseHero.vue";
import StatsSection from "./components/StatsSection.vue";
import MapTeaser from "./components/MapTeaser.vue";
import AppFooter from "./components/AppFooter.vue";

const countersActive = ref(false);
provide("countersActive", countersActive);

let observer;

onMounted(() => {
  const el = document.getElementById("stats");
  if (!el || typeof IntersectionObserver === "undefined") {
    countersActive.value = true;
    return;
  }
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          countersActive.value = true;
          observer?.disconnect();
        }
      });
    },
    { threshold: 0.25 }
  );
  observer.observe(el);
});

onUnmounted(() => {
  observer?.disconnect();
});
</script>

<template>
  <AppHeader />
  <div class="first-screen">
    <HeroSection />
  </div>
  <BrandStorySection />
  <main>
    <MenuSection />
    <TimelineSection />
    <CafesStrip />
    <FranchiseHero />
    <StatsSection />
    <MapTeaser />
  </main>
  <AppFooter />
</template>
