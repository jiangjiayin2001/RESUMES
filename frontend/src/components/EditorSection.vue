<template>
  <section class="editor-section">
    <header class="editor-section__head">
      <div>
        <p class="eyebrow eyebrow--dark">{{ eyebrow }}</p>
        <h2>{{ title }}</h2>
        <p v-if="description" class="editor-section__description">{{ description }}</p>
      </div>
      <div class="editor-section__actions">
        <slot name="actions" />
      </div>
    </header>
    <div class="editor-section__body">
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  description?: string
}>()
</script>
