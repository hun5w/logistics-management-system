<template>
  <div :class="['custom-card', `variant-${variant}`, `shadow-${shadow}`]">
    <div v-if="$slots.header || title" class="card-header">
      <slot name="header">
        <h3 v-if="title" class="card-title">{{ title }}</h3>
      </slot>
    </div>

    <div class="card-content">
      <slot></slot>
    </div>

    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: '',
  },
  variant: {
    type: String,
    default: 'default', // default | elevated | gradient
  },
  shadow: {
    type: String,
    default: 'md', // sm | md | lg | xl | accent
  },
})
</script>

<style scoped>
.custom-card {
  background-color: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-normal);

  /* 梯度背景叠加 hover 时淡入 */
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom right, rgba(0, 82, 255, 0.03), transparent);
    opacity: 0;
    transition: opacity var(--transition-normal);
    pointer-events: none;
  }

  &:hover {
    border-color: var(--color-accent);
    
    &::before {
      opacity: 1;
    }
  }
}

/* ============ 变体：Default ============ */
.custom-card.variant-default {
  box-shadow: var(--shadow-md);

  &:hover {
    box-shadow: var(--shadow-lg);
  }
}

/* ============ 变体：Elevated ============ */
.custom-card.variant-elevated {
  box-shadow: var(--shadow-lg);
  border-color: transparent;

  &:hover {
    box-shadow: var(--shadow-xl);
    transform: translateY(-4px);
  }
}

/* ============ 变体：Gradient Border（渐变边框） ============ */
.custom-card.variant-gradient {
  background: linear-gradient(to bottom right, var(--color-card), var(--color-card)) padding-box,
              linear-gradient(135deg, var(--color-accent), var(--color-accent-secondary)) border-box;
  border: 2px solid transparent;
  box-shadow: var(--shadow-accent-md);

  &:hover {
    box-shadow: var(--shadow-accent-lg);
    transform: translateY(-4px);
  }
}

/* ============ 阴影变体 ============ */
.custom-card.shadow-sm {
  box-shadow: var(--shadow-sm) !important;
}

.custom-card.shadow-lg {
  box-shadow: var(--shadow-lg) !important;
}

.custom-card.shadow-xl {
  box-shadow: var(--shadow-xl) !important;
}

.custom-card.shadow-accent {
  box-shadow: var(--shadow-accent-md) !important;
}

/* ============ 内部结构 ============ */
.card-header {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
  background-color: rgba(0, 82, 255, 0.02);
}

.card-title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 1.5rem;
  color: var(--color-foreground);
}

.card-content {
  padding: var(--spacing-lg);
}

.card-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--color-border);
  background-color: rgba(0, 82, 255, 0.01);
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
}

/* ============ 响应式 ============ */
@media (max-width: 640px) {
  .card-header,
  .card-content,
  .card-footer {
    padding: var(--spacing-md);
  }

  .card-title {
    font-size: 1.25rem;
  }
}
</style>
