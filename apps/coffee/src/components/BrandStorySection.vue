<template>
  <section class="story-section" id="story" aria-labelledby="story-heading">
    <header class="story-section__header">
      <div class="wrap story-section__header-inner">
        <span class="story-section__header-spacer" aria-hidden="true"></span>
        <h2 id="story-heading" class="story-section__title">История Кофе Феникс</h2>
        <span class="story-section__mark" aria-hidden="true" title="Кофе Феникс"></span>
      </div>
    </header>

    <div class="wrap story-section__grid">
      <div class="story-section__visual">
        <div class="story-section__bag-wrap">
          <img
            class="story-section__bag"
            src="/fonts/zernopackg.png"
            width="420"
            height="640"
            alt="Упаковка зерна Кофе Феникс"
            decoding="async"
            loading="lazy"
          />
        </div>
        <div class="story-section__cup-wrap">
          <img
            class="story-section__cup"
            src="/fonts/coffe.png"
            width="560"
            height="728"
            alt="Стакан Кофе Феникс"
            decoding="async"
            loading="lazy"
          />
        </div>
      </div>

      <div class="story-section__content">
        <p class="story-section__eyebrow">С 2026 года</p>
        <div class="story-section__body">
          <p>
            <strong class="story-section__brand">Кофе Феникс</strong> — для тех, кто ценит глубину
            вкуса, честную обжарку и спокойную силу утренней чашки — без компромиссов и суеты.
          </p>
          <p>
            Мы выстраиваем цепочку от отбора зерна до чашки: прозрачные партии, стабильные профили и
            уважение к бариста и гостю.
          </p>
          <p>
            Наша обжарка подчёркивает характер сырья — кислотность, сладость и послевкусие — чтобы
            каждый глоток ощущался завершённым ритуалом, а не случайным напитком.
          </p>
        </div>

        <ul class="story-section__features">
          <li class="story-section__feature">
            <span class="story-section__feature-title">Отборные зерна</span>
            <span class="story-section__feature-text">Надёжные фермы и контроль качества на входе.</span>
          </li>
          <li class="story-section__feature">
            <span class="story-section__feature-title">Мастерская обжарка</span>
            <span class="story-section__feature-text">Раскрываем глубину и естественный вкус партии.</span>
          </li>
          <li class="story-section__feature">
            <span class="story-section__feature-title">Выверенный вкус</span>
            <span class="story-section__feature-text">Баланс для повседневной чашки и особого повода.</span>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>
