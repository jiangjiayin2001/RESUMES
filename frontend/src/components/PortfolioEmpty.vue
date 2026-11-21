<template>
  <div class="portfolio-empty" :class="{ 'portfolio-empty--dark': dark }">
    <span>{{ number }}</span>
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  title: string
  description: string
  number?: string
  dark?: boolean
}>(), {
  number: '--',
  dark: false,
})
</script>

<style scoped>
.portfolio-empty {
  display: flex;
  min-height: 360px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 40px 24px;
  text-align: center;
  border: 1px dashed rgb(23 34 29 / 18%);
  border-radius: 5px;
  background: #f7f3ec;
}

.portfolio-empty--dark {
  color: #fff;
  border-color: rgb(255 255 255 / 16%);
  background: #17221d;
}

.portfolio-empty > span {
  color: #c56d48;
  font-family: Georgia, 'Songti SC', serif;
  font-size: 12px;
}

.portfolio-empty h2 {
  margin-top: 13px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(28px, 4vw, 45px);
  font-weight: 400;
  letter-spacing: -0.04em;
}

.portfolio-empty p {
  max-width: 380px;
  margin-top: 10px;
  color: #858b85;
  font-size: 10px;
  line-height: 1.8;
}

.portfolio-empty--dark p {
  color: rgb(255 255 255 / 48%);
}
</style>



