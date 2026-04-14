<template>
  <div class="basic-layout" :class="`basic-layout--${shellMode}`">
    <div class="basic-layout__wash basic-layout__wash--top"></div>
    <div class="basic-layout__wash basic-layout__wash--bottom"></div>
    <NavBar />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const shellMode = computed(() => route.meta.shell ?? 'public')
</script>

<style scoped>
.basic-layout {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.basic-layout__wash {
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  filter: blur(12px);
  pointer-events: none;
}

.basic-layout__wash--top {
  top: -120px;
  right: -80px;
  width: 340px;
  height: 340px;
  background: radial-gradient(circle, rgba(216, 165, 69, 0.2) 0%, rgba(216, 165, 69, 0) 70%);
}

.basic-layout__wash--bottom {
  left: -140px;
  bottom: -160px;
  width: 380px;
  height: 380px;
  background: radial-gradient(circle, rgba(31, 95, 174, 0.16) 0%, rgba(31, 95, 174, 0) 72%);
}

.basic-layout--student {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0%, rgba(238, 243, 248, 0.85) 100%);
}

.basic-layout--auth {
  background: linear-gradient(180deg, rgba(248, 251, 255, 0.94) 0%, rgba(238, 243, 248, 0.98) 100%);
}

.main-content {
  position: relative;
  z-index: 1;
  flex: 1;
}
</style>
