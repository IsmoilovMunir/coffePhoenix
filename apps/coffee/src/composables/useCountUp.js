import { ref, watch } from "vue";

function formatNum(n) {
  return new Intl.NumberFormat("ru-RU").format(Math.round(n));
}

export function animateCount(displayRef, end, duration) {
  const startTime = performance.now();

  function step(now) {
    const progress = Math.min((now - startTime) / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3);
    displayRef.value = formatNum(end * eased);
    if (progress < 1) {
      requestAnimationFrame(step);
    } else {
      displayRef.value = formatNum(end);
    }
  }

  requestAnimationFrame(step);
}

/**
 * @param {import('vue').Ref<boolean>} activeRef
 * @param {number} target
 * @param {number} durationMs
 */
export function useCountUp(activeRef, target, durationMs = 1600) {
  const display = ref(formatNum(0));
  let ran = false;

  watch(
    activeRef,
    (active) => {
      if (!active || ran) return;
      ran = true;
      animateCount(display, target, durationMs);
    },
    { flush: "post" }
  );

  return display;
}
