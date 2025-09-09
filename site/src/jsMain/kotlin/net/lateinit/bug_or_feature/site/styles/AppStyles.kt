package net.lateinit.bug_or_feature.site.styles

import org.jetbrains.compose.web.css.*

object AppStyles : StyleSheet() {
    init {
        ":root" style {
            // Light theme — 2025 trend palette (sky / cyan)
            property("--bg", "linear-gradient(180deg, #f7f8fb, #eef1f6)")
            property("--surface", "rgba(255,255,255,0.9)")
            property("--fg", "#0f172a")
            property("--muted", "#64748b")
            property("--border", "#e2e8f0")
            property("--radius", "16px")
            property("--accent", "#0EA5E9")
            property("--accent-fg", "#ffffff")
            property("--accent-soft", "rgba(14,165,233,0.18)")
            property("--accent-gradient", "linear-gradient(135deg, #0EA5E9, #60A5FA)")
        }

        media("(prefers-color-scheme: dark)") {
            ":root" style {
                // Dark theme
                property("--bg", "linear-gradient(180deg, #0b1020, #0b1220)")
                property("--surface", "rgba(18,20,28,0.64)")
                property("--fg", "#e6e6eb")
                property("--muted", "#9ba3af")
                property("--border", "#2b3245")
                property("--accent", "#22D3EE")
                property("--accent-fg", "#0b1020")
                property("--accent-soft", "rgba(34,211,238,0.22)")
                property("--accent-gradient", "linear-gradient(135deg, #22D3EE, #60A5FA)")
            }
        }

        // Manual theme override via data-theme attribute on <html>
        "[data-theme='light']" style {
            property("--bg", "linear-gradient(180deg, #f7f8fb, #eef1f6)")
            property("--surface", "rgba(255,255,255,0.9)")
            property("--fg", "#0f172a")
            property("--muted", "#64748b")
            property("--border", "#e2e8f0")
            property("--accent", "#0EA5E9")
            property("--accent-fg", "#ffffff")
            property("--accent-soft", "rgba(14,165,233,0.18)")
            property("--accent-gradient", "linear-gradient(135deg, #0EA5E9, #60A5FA)")
        }
        "[data-theme='dark']" style {
            property("--bg", "linear-gradient(180deg, #0b1020, #0b1220)")
            property("--surface", "rgba(18,20,28,0.64)")
            property("--fg", "#e6e6eb")
            property("--muted", "#9ba3af")
            property("--border", "#2b3245")
            property("--accent", "#22D3EE")
            property("--accent-fg", "#0b1020")
            property("--accent-soft", "rgba(34,211,238,0.22)")
            property("--accent-gradient", "linear-gradient(135deg, #22D3EE, #60A5FA)")
        }

        ".main-grid" style {
            property("display", "grid")
            property("grid-template-columns", "minmax(0,1.6fr) minmax(0,1fr)")
            property("gap", "24px")
            property("padding-bottom", "64px")
        }

        media(mediaMaxWidth(900.px)) {
            ".main-grid" style {
                property("grid-template-columns", "1fr")
            }
        }

        ".header" style {
            property("position", "sticky")
            property("top", "0")
            property("z-index", "10")
            property("background", "var(--surface)")
            property("backdrop-filter", "saturate(1.2) blur(8px)")
            property("border-bottom", "1px solid var(--border)")
            property("width", "100vw")
            property("margin-left", "calc(50% - 50vw)")
            property("margin-right", "calc(50% - 50vw)")
        }

        ".card" style {
            property("border", "1px solid var(--border)")
            property("border-radius", "18px")
            property("background", "var(--surface)")
            property("backdrop-filter", "blur(6px)")
            property("box-shadow", "0 2px 10px rgba(0,0,0,0.04)")
            property("transition", "box-shadow .2s ease, transform .2s ease")
        }
        ".card:hover" style {
            property("box-shadow", "0 8px 24px rgba(0,0,0,0.08)")
            property("transform", "translateY(-1px)")
        }

        ".input" style {
            property("padding", "10px 12px")
            property("border", "1px solid var(--border)")
            property("border-radius", "12px")
            property("background", "var(--surface)")
            property("color", "var(--fg)")
            property("outline", "none")
            property("transition", "border-color .15s ease, box-shadow .15s ease")
            property("width", "100%")
        }
        ".input:focus" style {
            property("border-color", "var(--accent)")
            property("box-shadow", "0 0 0 4px var(--accent-soft)")
        }

        ".pill" style {
            property("display", "inline-block")
            property("padding", "4px 8px")
            property("border-radius", "999px")
            property("border", "1px solid var(--border)")
            property("background", "var(--surface)")
            property("color", "var(--muted)")
            property("font-size", "12px")
        }

        ".logo" style {
            property("background", "var(--accent-gradient)")
            property("color", "#fff")
        }

        ".option-card" style {
            property("border", "1px solid var(--border)")
            property("border-radius", "18px")
            property("padding", "14px 16px")
            property("background", "var(--surface)")
            property("cursor", "pointer")
            property("transition", "transform .08s ease, box-shadow .16s ease, border-color .16s ease")
        }
        ".option-card:hover" style {
            property("box-shadow", "0 6px 16px rgba(0,0,0,0.06)")
            property("transform", "translateY(-1px)")
            property("border-color", "var(--accent)")
        }
        ".option-card.is-selected" style {
            property("border-color", "var(--accent)")
            property("box-shadow", "0 8px 24px var(--accent-soft)")
        }

        ".list-item" style {
            property("border", "1px solid var(--border)")
            property("border-radius", "16px")
            property("padding", "12px")
            property("background", "var(--surface)")
            property("transition", "background .12s ease, box-shadow .12s ease, transform .12s ease")
        }
        ".list-item:hover" style {
            property("background", "#fcfcfc")
            property("box-shadow", "0 4px 12px rgba(0,0,0,0.05)")
            property("transform", "translateY(-1px)")
        }

        ".footer" style {
            property("text-align", "center")
            property("color", "var(--muted)")
        }

        // Scrollbar styling
        "html" style {
            property("scrollbar-color", "var(--border) transparent")
            property("scrollbar-width", "thin")
        }
        "::-webkit-scrollbar" style { property("width", "10px"); property("height", "10px") }
        "::-webkit-scrollbar-track" style { property("background", "transparent") }
        "::-webkit-scrollbar-thumb" style {
            property("background", "var(--border)")
            property("border-radius", "999px")
            property("border", "2px solid transparent")
            property("background-clip", "padding-box")
        }

        ".btn" style {
            property("display", "inline-flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("gap", "8px")
            property("height", "36px")
            property("padding", "0 12px")
            property("border-radius", "10px")
            property("border", "1px solid var(--border)")
            property("background", "var(--surface)")
            property("color", "var(--fg)")
            property("font-weight", "600")
            property("transition", "transform .06s ease, box-shadow .12s ease, background .12s ease, border-color .12s ease")
        }
        ".btn:hover" style { property("box-shadow", "0 4px 12px rgba(0,0,0,0.06)") }
        ".btn:active" style { property("transform", "translateY(1px)") }

        ".btn-primary" style {
            property("background", "var(--accent)")
            property("color", "var(--accent-fg)")
            property("border-color", "var(--accent)")
        }
        ".btn-primary:hover" style { property("box-shadow", "0 6px 16px var(--accent-soft)") }

        ".btn-ghost" style {
            property("background", "#fff")
            property("color", "#111")
            property("border-color", "var(--border)")
        }

        ".line-clamp-2" style {
            property("display", "-webkit-box")
            property("-webkit-line-clamp", "2")
            property("-webkit-box-orient", "vertical")
            overflow("hidden")
        }
    }
}
