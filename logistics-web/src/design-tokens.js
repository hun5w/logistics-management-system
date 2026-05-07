/**
 * Design Token System - Minimalist Modern for Logistics Platform
 * 基于 Minimalist Modern 设计系统的电商物流平台专用令牌
 */

export const designTokens = {
  // 🎨 色彩系统
  colors: {
    // 基础色
    background: '#FAFAFA',      // 主背景
    foreground: '#0F172A',      // 主文字（Slate-900）
    muted: '#F1F5F9',           // 次要背景（Slate-100）
    mutedForeground: '#64748B', // 次要文字（Slate-500）
    
    // 品牌色 - Electric Blue 渐变
    accent: '#0052FF',          // 主品牌蓝
    accentSecondary: '#4D7CFF', // 渐变终点浅蓝
    accentForeground: '#FFFFFF',// 品牌色上的文字
    
    // 辅助
    border: '#E2E8F0',          // 边框色（Slate-200）
    card: '#FFFFFF',            // 卡片背景
    destructive: '#EF4444',     // 危险操作
    success: '#22C55E',         // 成功
    warning: '#F59E0B',         // 警告
    info: '#3B82F6',            // 信息
  },

  // 📐 间距系统（基于 4px）
  spacing: {
    xs: '0.25rem',    // 4px
    sm: '0.5rem',     // 8px
    md: '1rem',       // 16px
    lg: '1.5rem',     // 24px
    xl: '2rem',       // 32px
    '2xl': '2.5rem',  // 40px
    '3xl': '3rem',    // 48px
    '4xl': '4rem',    // 64px
  },

  // 🔤 排版系统
  typography: {
    fontFamily: {
      display: '"Calistoga", Georgia, serif',      // 标题
      body: '"Inter", system-ui, sans-serif',      // 正文
      mono: '"JetBrains Mono", monospace',         // 代码
    },
    fontSize: {
      xs: '0.75rem',    // 12px
      sm: '0.875rem',   // 14px
      base: '1rem',     // 16px
      lg: '1.125rem',   // 18px
      xl: '1.25rem',    // 20px
      '2xl': '1.5rem',  // 24px
      '3xl': '1.875rem',// 30px
      '4xl': '2.25rem', // 36px
      '5xl': '3rem',    // 48px
      '5xl-lg': '3.25rem', // 52px
    },
    fontWeight: {
      normal: 400,
      medium: 500,
      semibold: 600,
      bold: 700,
    },
    lineHeight: {
      tight: 1.05,
      normal: 1.15,
      relaxed: 1.625,
      loose: 1.75,
    },
  },

  // 🔲 圆角系统
  radius: {
    xs: '0.25rem',    // 4px
    sm: '0.5rem',     // 8px
    md: '0.75rem',    // 12px
    lg: '1rem',       // 16px
    xl: '1.5rem',     // 24px
    '2xl': '2rem',    // 32px
    full: '9999px',   // 完全圆形
  },

  // 🌑 阴影系统
  shadows: {
    sm: '0 1px 3px rgba(0,0,0,0.06)',
    md: '0 4px 6px rgba(0,0,0,0.07)',
    lg: '0 10px 15px rgba(0,0,0,0.08)',
    xl: '0 20px 25px rgba(0,0,0,0.1)',
    accentMd: '0 4px 14px rgba(0,82,255,0.25)',
    accentLg: '0 8px 24px rgba(0,82,255,0.35)',
  },

  // ⏱️ 动画配置
  animation: {
    duration: {
      fast: '150ms',
      normal: '200ms',
      slow: '300ms',
      slower: '700ms',
    },
    easing: {
      easeOut: 'cubic-bezier(0.16, 1, 0.3, 1)',
      easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
      linear: 'linear',
    },
  },

  // 📏 Z-index 层级
  zIndex: {
    hide: -1,
    auto: 'auto',
    base: 0,
    dropdown: 1000,
    sticky: 1020,
    fixed: 1030,
    modalBackdrop: 1040,
    modal: 1050,
    popover: 1060,
    tooltip: 1070,
  },
}

// CSS 变量映射（用于全局注入）
export const getCSSVariables = () => {
  return {
    '--color-background': designTokens.colors.background,
    '--color-foreground': designTokens.colors.foreground,
    '--color-muted': designTokens.colors.muted,
    '--color-muted-foreground': designTokens.colors.mutedForeground,
    '--color-accent': designTokens.colors.accent,
    '--color-accent-secondary': designTokens.colors.accentSecondary,
    '--color-accent-foreground': designTokens.colors.accentForeground,
    '--color-border': designTokens.colors.border,
    '--color-card': designTokens.colors.card,
    '--color-destructive': designTokens.colors.destructive,
    '--color-success': designTokens.colors.success,
    '--color-warning': designTokens.colors.warning,
    '--color-info': designTokens.colors.info,

    // 排版
    '--font-display': designTokens.typography.fontFamily.display,
    '--font-body': designTokens.typography.fontFamily.body,
    '--font-mono': designTokens.typography.fontFamily.mono,

    // 间距
    '--spacing-xs': designTokens.spacing.xs,
    '--spacing-sm': designTokens.spacing.sm,
    '--spacing-md': designTokens.spacing.md,
    '--spacing-lg': designTokens.spacing.lg,
    '--spacing-xl': designTokens.spacing.xl,

    // 动画
    '--animation-fast': designTokens.animation.duration.fast,
    '--animation-normal': designTokens.animation.duration.normal,
    '--animation-slow': designTokens.animation.duration.slow,
  }
}
