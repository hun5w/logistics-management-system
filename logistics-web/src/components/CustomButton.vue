<template>
  <button
    :class="[
      'custom-button',
      `variant-${variant}`,
      `size-${size}`,
      { 'is-loading': loading, 'is-disabled': disabled }
    ]"
    :disabled="disabled || loading"
    @click="$emit('click')"
  >
    <span v-if="loading" class="button-spinner"></span>
    <span class="button-content">
      <slot></slot>
      <svg
        v-if="showArrow && !loading"
        class="button-arrow"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
      >
        <line x1="5" y1="12" x2="19" y2="12"></line>
        <polyline points="12 5 19 12 12 19"></polyline>
      </svg>
    </span>
  </button>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary', // primary | secondary | outline | ghost
  },
  size: {
    type: String,
    default: 'md', // sm | md | lg
  },
  loading: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  showArrow: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['click'])
</script>

<style scoped>
.custom-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-family: var(--font-body);
  font-weight: 500;
  border: none;
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: all var(--transition-normal) var(--easing-out);
  outline: none;

  &:focus-visible {
    outline: 2px solid var(--color-accent);
    outline-offset: 2px;
  }

  &.is-disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

/* ============ 尺寸 ============ */
.custom-button.size-sm {
  height: 2.5rem; /* 40px */
  padding: 0 1rem;
  font-size: 0.875rem;
}

.custom-button.size-md {
  height: 3rem; /* 48px */
  padding: 0 1.5rem;
  font-size: 1rem;
}

.custom-button.size-lg {
  height: 3.5rem; /* 56px */
  padding: 0 2rem;
  font-size: 1.125rem;
}

/* ============ 变体：Primary（梯度 + 高对比） ============ */
.custom-button.variant-primary {
  background: linear-gradient(to right, var(--color-accent), var(--color-accent-secondary));
  color: var(--color-accent-foreground);
  box-shadow: var(--shadow-accent-md);

  &:hover:not(.is-disabled) {
    box-shadow: var(--shadow-accent-lg);
    transform: translateY(-2px);
    filter: brightness(1.1);
  }

  &:active:not(.is-disabled) {
    transform: scale(0.98);
  }
}

/* ============ 变体：Secondary（填充 + 柔和） ============ */
.custom-button.variant-secondary {
  background-color: var(--color-muted);
  color: var(--color-foreground);
  box-shadow: var(--shadow-sm);

  &:hover:not(.is-disabled) {
    background-color: #E8EEF5; /* 稍深 */
    box-shadow: var(--shadow-md);
    transform: translateY(-1px);
  }

  &:active:not(.is-disabled) {
    transform: scale(0.98);
  }
}

/* ============ 变体：Outline（边框 + 透明） ============ */
.custom-button.variant-outline {
  background-color: transparent;
  color: var(--color-accent);
  border: 1.5px solid var(--color-accent);
  box-shadow: none;

  &:hover:not(.is-disabled) {
    background-color: rgba(0, 82, 255, 0.03);
    border-color: var(--color-accent-secondary);
    box-shadow: var(--shadow-accent-md);
    transform: translateY(-1px);
  }

  &:active:not(.is-disabled) {
    transform: scale(0.98);
  }
}

/* ============ 变体：Ghost（无背景 + 文字） ============ */
.custom-button.variant-ghost {
  background-color: transparent;
  color: var(--color-muted-foreground);
  box-shadow: none;

  &:hover:not(.is-disabled) {
    color: var(--color-accent);
    background-color: rgba(0, 82, 255, 0.03);
  }

  &:active:not(.is-disabled) {
    transform: scale(0.98);
  }
}

/* ============ 加载状态 ============ */
.button-spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 600ms linear infinite;
}

.button-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.button-arrow {
  width: 1rem;
  height: 1rem;
  transition: transform var(--transition-normal);
}

.custom-button:hover .button-arrow:not(.is-disabled) {
  transform: translateX(4px);
}

/* ============ 动画 ============ */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
