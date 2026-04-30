<script setup>
defineProps({
  title: { type: String, required: true },
  text: { type: String, required: true },
  variant: { type: String, default: "bean" },
});
</script>

<template>
  <article class="feature-card">
    <div class="feature-card__icon" aria-hidden="true">
      <svg v-if="variant === 'bean'" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="16" cy="16" rx="9" ry="12" stroke="currentColor" stroke-width="1.5" />
        <path d="M13 10c2 6 7 14 11 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </svg>
      <svg v-else-if="variant === 'cup'" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M8 10h14v14a4 4 0 01-4 4H12a4 4 0 01-4-4V10z" stroke="currentColor" stroke-width="1.5" />
        <path d="M22 13h4a3 3 0 013 3v3a3 3 0 01-3 3h-4" stroke="currentColor" stroke-width="1.5" />
        <path d="M11 8h11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <path d="M14 6v3M19 6v3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </svg>
    </div>
    <h3 class="feature-card__title">{{ title }}</h3>
    <p class="feature-card__text">{{ text }}</p>
  </article>
</template>
