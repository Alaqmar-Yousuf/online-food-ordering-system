/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: 'var(--primary)',
        'primary-hover': 'var(--primary-hover)',
        'bg-dark': 'var(--bg-dark)',
        'bg-darker': 'var(--bg-darker)',
        'text-light': 'var(--text-light)',
        'text-muted': 'var(--text-muted)',
        'glass-bg': 'var(--glass-bg)',
        'glass-border': 'var(--glass-border)',
        'input-bg': 'var(--input-bg)',
        success: 'var(--success)',
        error: 'var(--error)',
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
      keyframes: {
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideInLeft: {
          '0%': { opacity: '0', transform: 'translateX(-30px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        pulseGlow: {
          '0%': { boxShadow: '0 0 0 0 rgba(255, 43, 94, 0.4)' },
          '70%': { boxShadow: '0 0 0 10px rgba(255, 43, 94, 0)' },
          '100%': { boxShadow: '0 0 0 0 rgba(255, 43, 94, 0)' },
        }
      },
      animation: {
        'fade-in-up': 'fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards',
        'slide-left': 'slideInLeft 0.5s ease-out forwards',
        'pulseGlow': 'pulseGlow 2s infinite',
      }
    },
  },
  plugins: [],
}
