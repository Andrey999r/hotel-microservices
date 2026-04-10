const SITE_KEY = import.meta.env.VITE_RECAPTCHA_SITE_KEY

/**
 * Returns a reCAPTCHA v3 token for the given action.
 * Falls back to empty string when grecaptcha is not loaded (local dev without key).
 */
export async function getRecaptchaToken(action = 'submit') {
    if (!SITE_KEY) {
        console.warn('[reCAPTCHA] VITE_RECAPTCHA_SITE_KEY is not set — skipping token')
        return ''
    }
    return new Promise((resolve) => {
        if (typeof window.grecaptcha === 'undefined' || !window.grecaptcha.execute) {
            console.warn('[reCAPTCHA] grecaptcha not ready')
            resolve('')
            return
        }
        window.grecaptcha.ready(async () => {
            try {
                const token = await window.grecaptcha.execute(SITE_KEY, { action })
                resolve(token)
            } catch (e) {
                console.error('[reCAPTCHA] execute error', e)
                resolve('')
            }
        })
    })
}
