<script setup>
import { ref, onUnmounted } from "vue";
import nasrinPhoto from "../assets/img/team/nasrin-karimovna.jpg";
import mihailPhoto from "../assets/img/team/mihail.png";
import sergeyPhoto from "../assets/img/team/sergey-petrovich.png";

const team = [
  {
    photo: nasrinPhoto,
    badge: "Основатель",
    name: "Насрин Каримовна",
    role: "Владелец Coffee Phoenix · франшиза «Феникс»",
    text: "Многие годы изучала кофейный бизнес и историю Виллы «Феникс». Объединила профессиональные команды и отборное зерно, создав Coffee Phoenix — проект, в котором соединяются любовь к кофе, гостеприимство и история «Феникса».",
  },
  {
    photo: mihailPhoto,
    name: "Михаил",
    role: "Финансовый директор, команда «Феникс»",
    text: "Предприниматель с более чем 15-летним опытом создания и развития бизнес-проектов с нуля. Сегодня курирует финансовую стратегию Coffee Phoenix и обеспечивает устойчивый рост экосистемы «Феникс».",
  },
  {
    photo: sergeyPhoto,
    name: "Сергей Петрович",
    role: "Директор по развитию · аналитик",
    text: "Более 18 лет в сфере делового развития и аналитики. Отвечает за стратегию расширения проекта, оценку рынков и принятие решений на основе данных.",
  },
];

const activeMember = ref(null);

function openMember(member) {
  activeMember.value = member;
}

function closeMember() {
  activeMember.value = null;
}

function handleKeydown(event) {
  if (event.key === "Escape") closeMember();
}

window.addEventListener("keydown", handleKeydown);
onUnmounted(() => window.removeEventListener("keydown", handleKeydown));
</script>

<template>
  <section class="team-section" id="team" aria-labelledby="team-heading">
    <div class="wrap">
      <p class="section-sub center">Команда</p>
      <h2 id="team-heading" class="section-title center">Люди за чашкой Coffee Phoenix</h2>
      <p class="lead center">
        Команда, которая отвечает за вкус, сервис и развитие сети «Феникс».
      </p>
      <div class="team-section__grid">
        <article class="team-card" v-for="member in team" :key="member.name">
          <button
            type="button"
            class="team-card__photo-wrap"
            :aria-label="`Открыть фото: ${member.name}`"
            @click="openMember(member)"
          >
            <img
              class="team-card__photo"
              :src="member.photo"
              :alt="member.name"
              width="240"
              height="240"
              decoding="async"
              loading="lazy"
              draggable="false"
            />
          </button>
          <p v-if="member.badge" class="team-card__badge">{{ member.badge }}</p>
          <p class="team-card__name">{{ member.name }}</p>
          <p class="team-card__role">{{ member.role }}</p>
          <p class="team-card__text">{{ member.text }}</p>
        </article>
      </div>
    </div>

    <Teleport to="body">
      <div
        v-if="activeMember"
        class="team-modal"
        role="dialog"
        aria-modal="true"
        @click.self="closeMember"
      >
        <div class="team-modal__card">
          <button
            type="button"
            class="team-modal__close"
            aria-label="Закрыть"
            @click="closeMember"
          >
            ✕
          </button>
          <img
            class="team-modal__photo"
            :src="activeMember.photo"
            :alt="activeMember.name"
            draggable="false"
          />
          <div class="team-modal__body">
            <p v-if="activeMember.badge" class="team-card__badge">{{ activeMember.badge }}</p>
            <p class="team-modal__name">{{ activeMember.name }}</p>
            <p class="team-card__role">{{ activeMember.role }}</p>
            <p class="team-modal__text">{{ activeMember.text }}</p>
          </div>
        </div>
      </div>
    </Teleport>
  </section>
</template>
