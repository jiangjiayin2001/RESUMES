<template>
  <div class="avatar-uploader">
    <div
      class="avatar-uploader__preview"
      :class="{ 'avatar-uploader__preview--dragging': dragging }"
      role="button"
      tabindex="0"
      @click="openPicker"
      @keydown.enter.prevent="openPicker"
      @keydown.space.prevent="openPicker"
      @dragover.prevent="dragging = true"
      @dragleave.prevent="dragging = false"
      @drop.prevent="handleDrop"
    >
      <img v-if="previewUrl" :src="previewUrl" alt="头像预览" />
      <span v-else class="avatar-uploader__placeholder">
        <UploadCloud :size="24" />
        <b>点击上传头像</b>
        <small>或拖拽图片到这里</small>
      </span>
      <span v-if="uploading" class="avatar-uploader__loading">
        <LoaderCircle class="spin" :size="23" />
        上传中
      </span>
    </div>

    <div class="avatar-uploader__copy">
      <strong>个人照片</strong>
      <p>支持 JPG、PNG、WebP，最大 8 MB。上传完成后点击右上角“保存”生效。</p>
      <p v-if="errorMessage" class="avatar-uploader__error">{{ errorMessage }}</p>
      <div class="avatar-uploader__actions">
        <button class="button button--soft button--small" type="button" :disabled="uploading" @click="openPicker">
          <Upload :size="15" /> {{ modelValue ? '更换图片' : '选择图片' }}
        </button>
        <button v-if="modelValue" class="button button--ghost button--small" type="button" :disabled="uploading" @click="removeImage">
          移除
        </button>
      </div>
    </div>

    <input ref="fileInput" class="avatar-uploader__input" type="file" accept="image/jpeg,image/png,image/webp" @change="handleChange" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { LoaderCircle, Upload, UploadCloud } from 'lucide-vue-next'
import { uploadImage } from '@/api/upload'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const dragging = ref(false)
const errorMessage = ref('')

const previewUrl = computed(() => {
  if (!props.modelValue) return ''
  if (props.modelValue.startsWith('/api/public/uploads/')) return props.modelValue
  try {
    const url = new URL(props.modelValue)
    return url.protocol === 'http:' || url.protocol === 'https:' ? url.toString() : ''
  } catch {
    return ''
  }
})

function openPicker() {
  if (!uploading.value) fileInput.value?.click()
}

function handleChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (file) void processFile(file)
  input.value = ''
}

function handleDrop(event: DragEvent) {
  dragging.value = false
  const file = event.dataTransfer?.files?.[0]
  if (file) void processFile(file)
}

async function processFile(file: File) {
  errorMessage.value = ''
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
    errorMessage.value = '仅支持 JPG、PNG 或 WebP 图片'
    return
  }
  if (file.size > 8 * 1024 * 1024) {
    errorMessage.value = '图片不能超过 8 MB'
    return
  }

  uploading.value = true
  try {
    const result = await uploadImage(file)
    emit('update:modelValue', result.url)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '图片上传失败'
  } finally {
    uploading.value = false
  }
}

function removeImage() {
  emit('update:modelValue', '')
}
</script>
