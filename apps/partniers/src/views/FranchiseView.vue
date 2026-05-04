<script setup>
import { ref, nextTick, watch } from 'vue'

// Без Date.now(): иначе каждый remount срывает кеш и страница дольше «доезжает».
const frameSrc = '/legacy/html/franchise.html'
const frameReady = ref(false)
const legacyFrameRef = ref(null)

const leadModalOpen = ref(false)
const leadModalFormRef = ref(null)
const leadModalFeedback = ref('hidden')
const leadModalErrorMsg = ref('')
const leadModalSubmitting = ref(false)

let leadModalEscHandler = null

const closeLeadModal = () => {
  leadModalOpen.value = false
}

const openLeadModal = () => {
  leadModalFeedback.value = 'hidden'
  leadModalErrorMsg.value = ''
  leadModalOpen.value = true
}

watch(leadModalOpen, (open) => {
  if (open) {
    leadModalEscHandler = (e) => {
      if (e.key === 'Escape') {
        e.preventDefault()
        closeLeadModal()
      }
    }
    document.addEventListener('keydown', leadModalEscHandler)
    nextTick(() => {
      document.getElementById('feniks-modal-fullname')?.focus()
    })
  } else if (leadModalEscHandler) {
    document.removeEventListener('keydown', leadModalEscHandler)
    leadModalEscHandler = null
  }
})

const submitLeadModal = async () => {
  const form = leadModalFormRef.value
  if (!form) return
  if (!form.checkValidity()) {
    form.reportValidity()
    return
  }
  const fd = new FormData(form)
  const payload = {
    fullName: String(fd.get('fullName') ?? '').trim(),
    phone: String(fd.get('phone') ?? '').trim(),
    email: String(fd.get('email') ?? '').trim(),
    city: String(fd.get('city') ?? '').trim(),
  }
  const baseUrl = import.meta.env.VITE_FRANCHISE_LEAD_URL
  const url =
    typeof baseUrl === 'string' && baseUrl.length > 0 ? baseUrl : '/api/v1/franchise/leads'
  leadModalFeedback.value = 'hidden'
  leadModalErrorMsg.value = ''
  leadModalSubmitting.value = true
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
    if (res.ok) {
      form.reset()
      leadModalFeedback.value = 'success'
      return
    }
    let message = 'Попробуйте позже или напишите нам на почту.'
    try {
      const err = await res.json()
      if (err?.message) message = err.message
    } catch {
      /* ignore */
    }
    leadModalErrorMsg.value = message
    leadModalFeedback.value = 'error'
  } catch {
    leadModalErrorMsg.value = 'Нет связи с сервером. Проверьте подключение к интернету.'
    leadModalFeedback.value = 'error'
  } finally {
    leadModalSubmitting.value = false
  }
}

const applyHeroBackground = (event) => {
  const iframeEl = event?.target
  const doc = iframeEl?.contentDocument
  if (!doc) return

  // Раньше opacity:1 ставился в самом конце функции — iframe был невидим, пока не отработают все патчи и observer'ы.
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      frameReady.value = true
    })
  })

  const setText = (selectors, value) => {
    selectors.forEach((selector) => {
      doc.querySelectorAll(selector).forEach((el) => {
        el.textContent = value
      })
    })
  }

  let styleNode = doc.getElementById('feniks-title-bg-override')
  if (!styleNode) {
    styleNode = doc.createElement('style')
    styleNode.id = 'feniks-title-bg-override'
    doc.head.appendChild(styleNode)
  }

  styleNode.textContent = `
    @import url('https://fonts.googleapis.com/css2?family=Philosopher:wght@400;700&display=swap');

    :root {
      --feniks-green: #093333;
      --feniks-beige: #E4C49E;
      --feniks-ivory: #EEE9E3;
      --feniks-graphite: #20201F;
    }

    /* html + корень Next: иначе после зелёного футера при доскролле виден белый фон layout */
    html {
      background: var(--feniks-green) !important;
      min-height: 100%;
      overscroll-behavior-y: none !important;
      overflow-x: hidden !important;
    }
    body {
      background: var(--feniks-green) !important;
      color: var(--feniks-graphite) !important;
      font-family: Candara, "Philosopher", "Segoe UI", Arial, sans-serif !important;
      min-height: 100%;
      overscroll-behavior-y: none !important;
      overflow-x: hidden !important;
    }
    #__next,
    [class*="Layout_Layout"] {
      background: var(--feniks-green) !important;
      min-height: 100%;
      overscroll-behavior-y: none !important;
    }

    h1, h2, h3, h4, [class*="Title__"], [class*="Heading__"], [class*="Label__"] {
      font-family: "Philosopher", Candara, "Segoe UI", Arial, sans-serif !important;
      color: var(--feniks-green) !important;
      letter-spacing: 0.01em !important;
    }
    [class*="Franchise_"] h2 {
      color: #EEE9E3 !important;
    }
    .Franchise_ImageLogo__bwWWt img,
    [class*="Franchise_ImageLogo"] img {
      width: 72% !important;
      height: auto !important;
      object-fit: contain !important;
      inset: 0 !important;
      margin: 0 !important;
      transform: translateY(-18px) !important;
      transform-origin: left top !important;
    }

    p, span, a, button, input, textarea, li, div {
      font-family: Candara, "Philosopher", "Segoe UI", Arial, sans-serif !important;
    }

    [class*="Container_Container"] {
      width: min(1240px, calc(100% - 40px)) !important;
      margin-left: auto !important;
      margin-right: auto !important;
    }

    .ThemeSelector_Wrapper__f7vGu,
    [class*="ThemeSelector_Wrapper"] {
      display: none !important;
      visibility: hidden !important;
      opacity: 0 !important;
      pointer-events: none !important;
    }

    /* Шапка сайта OPC внутри iframe — своя шапка в Vue */
    [class*="Header_Wrapper"],
    [data-container="header-white"] {
      display: none !important;
      visibility: hidden !important;
      opacity: 0 !important;
      pointer-events: none !important;
      height: 0 !important;
      overflow: hidden !important;
    }
    /* После удаления legacy-хедера убираем резерв под него в layout */
    [class*="Layout_Main__"],
    main[data-container="scroll"],
    [class*="Layout_Site__"],
    [class*="Layout_Content__"] {
      margin-top: 0 !important;
      padding-top: 0 !important;
    }
    /* Bitrix/Navigation modal wrappers идут после </main> и добавляют высоту страницы.
       Делаем их overlay-слоем, чтобы не появлялся «хвост» после футера. */
    [class*="Bitrix"][class*="_Wrap__"],
    [class*="NavigationModal_Wrap__"] {
      position: fixed !important;
      inset: 0 !important;
      width: 100% !important;
      height: 100dvh !important;
      max-height: 100dvh !important;
      min-height: 0 !important;
      margin: 0 !important;
    }
    .TitleScreen_BgImg__XyLRp.TitleScreen_Standart__fktEu {
      background-image: url('/legacy/images/939.png') !important;
      background-size: cover !important;
      background-position: center !important;
      background-repeat: no-repeat !important;
      border-radius: 24px !important;
      overflow: hidden !important;
      box-shadow: 0 18px 40px rgba(32, 32, 31, 0.2) !important;
      opacity: 1 !important;
      filter: none !important;
      backdrop-filter: none !important;
    }
    .TitleScreen_BgImg__XyLRp.TitleScreen_Standart__fktEu::before,
    .TitleScreen_BgImg__XyLRp.TitleScreen_Standart__fktEu::after {
      content: none !important;
      display: none !important;
      opacity: 0 !important;
      filter: none !important;
      backdrop-filter: none !important;
      background: transparent !important;
    }
    .TitleScreen_Logo__2mxVl img {
      object-fit: contain !important;
    }
    .TitleScreen_Logo__2mxVl {
      transform: translateY(-14px) scale(1.14) !important;
      transform-origin: center top !important;
    }
    .TitleScreen_Description__3BEJx {
      color: #EEE9E3 !important;
      text-shadow: 0 6px 20px rgba(0, 0, 0, 0.35) !important;
      letter-spacing: 0.02em !important;
      line-height: 1.3 !important;
      max-width: 960px !important;
      margin: 0 auto !important;
      text-transform: none !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-grid {
      display: grid !important;
      grid-template-columns: minmax(0, 1fr) 240px !important;
      gap: clamp(16px, 2.2vw, 30px) !important;
      align-items: center !important;
      transform: translateY(-26px) !important;
      background: linear-gradient(
        180deg,
        rgba(9, 51, 51, 0.50) 0%,
        rgba(9, 51, 51, 0.70) 52%,
        rgba(9, 51, 51, 0.96) 100%
      ) !important;
      border: 1px solid rgba(238, 233, 227, 0.14) !important;
      border-radius: 20px !important;
      padding: clamp(16px, 2vw, 28px) !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-copy {
      text-align: left !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-side-logo {
      display: flex !important;
      justify-content: center !important;
      align-items: center !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-side-logo img {
      width: min(280px, 100%) !important;
      height: auto !important;
      object-fit: contain !important;
      filter: drop-shadow(0 8px 18px rgba(0, 0, 0, 0.24)) !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-title {
      display: block;
      font-family: "Philosopher", Candara, "Segoe UI", Arial, sans-serif !important;
      font-size: clamp(28px, 4.2vw, 52px) !important;
      font-weight: 700 !important;
      color: #E4C49E !important;
      letter-spacing: 0.02em !important;
      margin-bottom: 8px !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-line {
      display: block;
      font-size: clamp(17px, 1.8vw, 25px) !important;
      font-weight: 600 !important;
      color: #EEE9E3 !important;
      margin-bottom: 2px !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-brand {
      display: block;
      font-size: clamp(18px, 2vw, 28px) !important;
      font-weight: 700 !important;
      color: #E4C49E !important;
      margin: 4px 0 !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-subline {
      display: block;
      font-size: clamp(16px, 1.5vw, 21px) !important;
      color: #EEE9E3 !important;
    }
    .TitleScreen_Description__3BEJx .feniks-hero-note {
      display: block;
      grid-column: 1 / -1 !important;
      margin-top: 6px !important;
      font-size: clamp(15px, 1.3vw, 19px) !important;
      font-weight: 500 !important;
      color: #EEE9E3 !important;
      text-align: center !important;
    }
    @media (max-width: 860px) {
      .TitleScreen_Description__3BEJx .feniks-hero-grid {
        grid-template-columns: 1fr !important;
      }
      .TitleScreen_Description__3BEJx .feniks-hero-copy {
        text-align: center !important;
      }
      .TitleScreen_Description__3BEJx .feniks-hero-side-logo img {
        width: min(180px, 70%) !important;
      }
    }
    @media (max-width: 1024px) {
      .Franchise_Wrap__Wogmk,
      [class*="Franchise_Wrap"] {
        padding: 36px 24px !important;
      }
      .Franchise_LeftContentBlock__YKxml h2,
      [class*="Franchise_LeftContentBlock"] h2 {
        font-size: 42px !important;
        line-height: 1.05 !important;
      }
      .Franchise_ImageLogo__bwWWt img,
      [class*="Franchise_ImageLogo"] img {
        width: 82% !important;
        transform: translateY(-10px) !important;
      }
      .Franchise_LeftContentBlock__YKxml .Franchise_Text__TpL7Z,
      [class*="Franchise_LeftContentBlock"] [class*="Franchise_Text"] {
        margin-top: 72px !important;
        font-size: 22px !important;
        line-height: 1.25 !important;
      }
      .Franchise_LeftContentBlock__YKxml .Franchise_SmallText__8RbIO,
      [class*="Franchise_LeftContentBlock"] [class*="Franchise_SmallText"] {
        margin-top: 14px !important;
        font-size: 13px !important;
      }
      .Franchise_WrapBtns__6_oxF,
      [class*="Franchise_WrapBtns"] {
        margin-top: 22px !important;
        gap: 12px !important;
      }
      .Franchise_LinesContainer__mzmOP .Franchise_Text__TpL7Z,
      [class*="Franchise_LinesContainer"] [class*="Franchise_Text"] {
        font-size: 28px !important;
      }
      .Franchise_BubbleWrap__ZBQrk,
      [class*="Franchise_BubbleWrap"] {
        margin-top: 24px !important;
      }
      .Franchise_BubbleWrap__ZBQrk .Franchise_BubbleItem__aRK86,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_BubbleItem"] {
        min-width: 172px !important;
        min-height: 172px !important;
      }
      .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_More__9hQvQ,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_More"] {
        font-size: 19px !important;
      }
      .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_Description__D94hr,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_Description"] {
        font-size: 9px !important;
      }
    }
    @media (max-width: 743px) {
      .Franchise_Wrap__Wogmk,
      [class*="Franchise_Wrap"] {
        padding: 28px 16px !important;
      }
      .Franchise_LeftContentBlock__YKxml h2,
      [class*="Franchise_LeftContentBlock"] h2 {
        font-size: 32px !important;
        margin-bottom: 8px !important;
      }
      .Franchise_ImageLogo__bwWWt img,
      [class*="Franchise_ImageLogo"] img {
        width: 88% !important;
        transform: translateY(-4px) !important;
      }
      .Franchise_LeftContentBlock__YKxml .Franchise_Text__TpL7Z,
      [class*="Franchise_LeftContentBlock"] [class*="Franchise_Text"] {
        margin-top: 44px !important;
        font-size: 18px !important;
        line-height: 1.25 !important;
      }
      .Franchise_LeftContentBlock__YKxml .Franchise_SmallText__8RbIO,
      [class*="Franchise_LeftContentBlock"] [class*="Franchise_SmallText"] {
        margin-top: 10px !important;
        font-size: 12px !important;
        line-height: 1.35 !important;
      }
      .Franchise_WrapBtns__6_oxF,
      [class*="Franchise_WrapBtns"] {
        width: 100% !important;
      }
      .Franchise_WrapBtns__6_oxF > div,
      [class*="Franchise_WrapBtns"] > div {
        width: 100% !important;
      }
      .Franchise_WrapBtns__6_oxF button,
      [class*="Franchise_WrapBtns"] button {
        width: 100% !important;
        min-height: 52px !important;
        font-size: 14px !important;
      }
      .Franchise_LinesContainer__mzmOP .Franchise_Text__TpL7Z,
      [class*="Franchise_LinesContainer"] [class*="Franchise_Text"] {
        font-size: 22px !important;
      }
      .Franchise_BubbleWrap__ZBQrk,
      [class*="Franchise_BubbleWrap"] {
        display: grid !important;
        grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
        gap: 10px !important;
      }
      .Franchise_BubbleWrap__ZBQrk .Franchise_BubbleItem__aRK86,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_BubbleItem"] {
        min-width: 0 !important;
        width: min(100%, 44vw) !important;
        max-width: 168px !important;
        aspect-ratio: 1 / 1 !important;
        height: auto !important;
        min-height: unset !important;
        border-radius: 50% !important;
        justify-self: center !important;
        margin-inline: auto !important;
        padding: 12px !important;
        display: flex !important;
        flex-direction: column !important;
        align-items: center !important;
        justify-content: center !important;
        text-align: center !important;
      }
      .Franchise_BubbleWrap__ZBQrk .Franchise_More__9hQvQ,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_More"] {
        transform: translateY(-10px) !important;
        font-size: 11px !important;
      }
      .Franchise_BubbleWrap__ZBQrk .Franchise_Name__G1zSl,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Name"] {
        font-size: 16px !important;
      }
      .Franchise_BubbleWrap__ZBQrk .Franchise_Description__D94hr,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Description"] {
        font-size: 11px !important;
        line-height: 1.2 !important;
      }
      .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_Name__G1zSl,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_Name"] {
        font-size: 11px !important;
      }
      .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_More__9hQvQ,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_More"] {
        font-size: max(8px, calc(11px - 5px)) !important;
      }
      .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_Description__D94hr,
      [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_Description"] {
        font-size: max(8px, calc(11px - 5px)) !important;
      }
    }
    .TitleScreen_Buttons__0Vevh button:first-child {
      background: var(--feniks-green) !important;
      border-color: var(--feniks-green) !important;
      color: var(--feniks-ivory) !important;
      border-radius: 12px !important;
      box-shadow: 0 8px 22px rgba(9, 51, 51, 0.22) !important;
    }
    .TitleScreen_Buttons__0Vevh button:nth-child(2) {
      background: var(--feniks-beige) !important;
      border-color: var(--feniks-beige) !important;
      color: #20201f !important;
      border-radius: 12px !important;
    }
    .TitleScreen_Buttons__0Vevh button:nth-child(2) span,
    .TitleScreen_Buttons__0Vevh button:nth-child(2) .TitleScreen_Icon__XLj_z {
      color: #20201f !important;
    }
    .TitleScreen_Buttons__0Vevh {
      justify-content: flex-start !important;
      align-items: center !important;
      margin-left: 0 !important;
      margin-right: auto !important;
    }

    button, [class*="Button_Button"] {
      border-radius: 12px !important;
    }

    [class*="Formats_Wrap"],
    [class*="Conditions_Wrap"],
    [class*="Calculator_Wrap"],
    [class*="Form_Wrap"],
    [class*="About_Wrap"],
    [class*="Advantages_Wrap"],
    [class*="Franchise_Wrap"] {
      position: relative !important;
    }
    .Franchise_Container__8Ym8N,
    .Franchise_Wrap__Wogmk,
    [class*="Franchise_Container"],
    [class*="Franchise_Wrap"] {
      background: #093333 !important;
    }
    .Franchise_LeftContentBlock__YKxml .Franchise_Text__TpL7Z,
    [class*="Franchise_LeftContentBlock"] [class*="Franchise_Text"] {
      margin-top: 118px !important;
    }

    [class*="Formats_Wrap"] [class*="Formats_Title"],
    [class*="Conditions_Wrap"] [class*="Conditions_Title"],
    [class*="Calculator_Wrap"] [class*="Calculator_Title"],
    [class*="Form_Wrap"] [class*="Form_Title"],
    [class*="About_Wrap"] [class*="About_Title"] {
      color: var(--feniks-green) !important;
    }

    [class*="FormatCard_Wrap"],
    [class*="Card_Wrap"],
    [class*="Item_Wrap"] {
      background: #fff !important;
      border: 1px solid rgba(9, 51, 51, 0.1) !important;
      border-radius: 18px !important;
      box-shadow: 0 10px 26px rgba(32, 32, 31, 0.08) !important;
      overflow: hidden !important;
    }

    [class*="FormatCard_Title"],
    [class*="FormatCard_Area"],
    [class*="FormatCard_Label"],
    [class*="FormatCard_Value"] {
      color: var(--feniks-graphite) !important;
    }

    [class*="Conditions_Cards"] [class*="Card_Green"] {
      background-color: #093333 !important;
      background-image: none !important;
      border: 1px solid rgba(228, 196, 158, 0.25) !important;
      box-shadow: 0 14px 32px rgba(9, 51, 51, 0.35) !important;
    }
    [class*="Conditions_Cards"] [class*="Card_Green"] [class*="Card_Label"] {
      border-color: #fff !important;
      color: #fff !important;
    }
    [class*="Conditions_Cards"] [class*="Card_Green"] [class*="Card_Price"],
    [class*="Conditions_Cards"] [class*="Card_Green"] [class*="Card_Text"] {
      color: #eee9e3 !important;
    }
    [class*="Conditions_Cards"] [class*="Card_Green"] [class*="Card_ArrowIcon"] {
      filter: none !important;
      opacity: 0.95 !important;
    }
    [class*="Conditions_Cards"] [class*="Card_Green"] .feniks-kp-intro {
      color: rgba(238, 233, 227, 0.95) !important;
    }

    [class*="Conditions_Cards"] .feniks-kp-details {
      margin-top: 14px;
      padding: 10px 12px;
      border-radius: 12px;
      border: 1px solid rgba(9, 51, 51, 0.18);
      background: #e4c49e !important;
      text-align: left;
    }
    [class*="Conditions_Cards"] .feniks-kp-details summary {
      font-weight: 600;
      cursor: pointer;
      list-style: none;
      font-size: 15px;
      color: var(--feniks-green) !important;
    }
    [class*="Conditions_Cards"] .feniks-kp-details summary::-webkit-details-marker {
      display: none;
    }
    [class*="Conditions_Cards"] .feniks-conditions-list {
      margin: 10px 0 0;
      padding-left: 1.2rem;
      font-size: 13px;
      line-height: 1.45;
      color: inherit;
    }
    [class*="Conditions_Cards"] [class*="Card_White"] .feniks-conditions-list {
      color: var(--feniks-graphite);
    }
    [class*="Conditions_Cards"] [class*="Card_Green"] .feniks-conditions-list {
      color: var(--feniks-graphite) !important;
    }
    [class*="Conditions_Cards"] .feniks-kp-intro {
      font-size: 14px !important;
      line-height: 1.45 !important;
      text-align: left !important;
    }

    .Franchise_BubbleWrap__ZBQrk .Franchise_Name__G1zSl,
    [class*="Franchise_BubbleWrap"] [class*="Franchise_Name"] {
      font-size: clamp(24px, 2vw, 34px) !important;
      font-weight: 700 !important;
      line-height: 1.1 !important;
      white-space: nowrap !important;
      letter-spacing: 0 !important;
    }
    .Franchise_BubbleWrap__ZBQrk .Franchise_More__9hQvQ,
    [class*="Franchise_BubbleWrap"] [class*="Franchise_More"] {
      transform: translateY(-24px) !important;
    }


    /* Пузырь «Срок окупаемости» (Bubble5): на 5px меньше остальных */
    .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_Name__G1zSl,
    [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_Name"] {
      font-size: clamp(19px, 2vw, 29px) !important;
    }
    .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_More__9hQvQ,
    [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_More"] {
      font-size: 23px !important;
    }
    .Franchise_BubbleWrap__ZBQrk [class*="Franchise_Bubble5"] .Franchise_Description__D94hr,
    [class*="Franchise_BubbleWrap"] [class*="Franchise_Bubble5"] [class*="Franchise_Description"] {
      font-size: 9px !important;
      line-height: 1.2 !important;
    }

    [class*="FormatCard_Sum"] {
      background: var(--feniks-ivory) !important;
      border-top: 1px solid rgba(9, 51, 51, 0.1) !important;
    }

    [class*="swiper-slide"] {
      height: auto !important;
    }

    input, textarea, select, [class*="Input_Input"], [class*="Form_Input"] {
      border-radius: 12px !important;
      border: 1px solid rgba(9, 51, 51, 0.24) !important;
      background: #fff !important;
      color: var(--feniks-graphite) !important;
    }

    input:focus, textarea:focus, select:focus {
      outline: 2px solid rgba(9, 51, 51, 0.22) !important;
      outline-offset: 1px !important;
      border-color: var(--feniks-green) !important;
    }

    [class*="Form_"] [class*="Button_Green"],
    [class*="Calculator_"] [class*="Button_Green"] {
      background: var(--feniks-green) !important;
      border-color: var(--feniks-green) !important;
      color: var(--feniks-ivory) !important;
    }

    /* Bitrix24 форма в блоке контактов: жёсткий адаптив */
    #contacts .b24-form,
    #contacts .b24-form-dark,
    #contacts .b24-form-wrapper,
    #contacts .b24-form-content,
    #contacts .b24-form-padding-side,
    #contacts .b24-form-state-container,
    #contacts form {
      width: 100% !important;
      max-width: 100% !important;
      box-sizing: border-box !important;
    }
    #contacts .b24-form-wrapper {
      border-radius: 16px !important;
      overflow: hidden !important;
      box-shadow: 0 10px 24px rgba(9, 51, 51, 0.18) !important;
    }
    #contacts .b24-form-content {
      padding: 18px !important;
    }
    #contacts .b24-form-field {
      margin-bottom: 12px !important;
    }
    #contacts .b24-form-control-container {
      width: 100% !important;
    }
    #contacts .b24-form-control {
      width: 100% !important;
      min-height: 50px !important;
      height: 50px !important;
      border-radius: 12px !important;
      padding: 14px 14px !important;
      font-size: 16px !important; /* iOS: без auto-zoom */
      line-height: 1.25 !important;
      box-sizing: border-box !important;
      border: 1px solid rgba(9, 51, 51, 0.24) !important;
      background: #fff !important;
      color: #20201f !important;
    }
    #contacts .b24-form-control-label {
      line-height: 1.2 !important;
      font-size: 13px !important;
      max-width: calc(100% - 20px) !important;
      white-space: nowrap !important;
      overflow: hidden !important;
      text-overflow: ellipsis !important;
    }
    #contacts .b24-form-field-agreement label {
      display: flex !important;
      align-items: flex-start !important;
      gap: 8px !important;
    }
    #contacts .b24-form-field-agreement input[type='checkbox'] {
      margin-top: 2px !important;
      flex: 0 0 auto !important;
    }
    #contacts .b24-form-field-agreement .b24-form-control-desc {
      display: inline-block !important;
      font-size: 12px !important;
      line-height: 1.35 !important;
      white-space: normal !important;
      word-break: break-word !important;
    }
    #contacts .b24-form-btn-container,
    #contacts .b24-form-btn-block,
    #contacts .b24-form-btn {
      width: 100% !important;
    }
    #contacts .b24-form-btn {
      min-height: 50px !important;
      border-radius: 12px !important;
      font-size: 15px !important;
      font-weight: 600 !important;
      background: #093333 !important;
      color: #eee9e3 !important;
      border: 1px solid #093333 !important;
    }
    /* Bitrix / invisible reCAPTCHA — не показываем (своя отправка через API) */
    .b24-form-recaptcha,
    .grecaptcha-badge,
    textarea.g-recaptcha-response,
    iframe[title='reCAPTCHA'] {
      display: none !important;
      visibility: hidden !important;
      width: 0 !important;
      height: 0 !important;
      max-height: 0 !important;
      overflow: hidden !important;
      opacity: 0 !important;
      pointer-events: none !important;
      position: absolute !important;
      clip: rect(0, 0, 0, 0) !important;
      border: 0 !important;
      margin: 0 !important;
      padding: 0 !important;
    }

    /* Bitrix-обёртка после подмены на нашу форму */
    .b24-form-wrapper.feniks-replaced-b24-lead {
      background: transparent !important;
      box-shadow: none !important;
      padding: 0 !important;
      max-width: 760px;
      margin-left: auto;
      margin-right: auto;
    }

    /* Кастомная форма заявки (вместо Bitrix) */
    #contacts .feniks-form-header, .feniks-lead-root .feniks-form-header {
      text-align: center;
      margin: 0 auto clamp(14px, 2vw, 22px);
      max-width: 760px;
      padding: clamp(18px, 2.5vw, 28px) clamp(16px, 3vw, 32px);
      border-radius: 18px;
      background: var(--feniks-green) !important;
      border: 1px solid rgba(228, 196, 158, 0.28);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.22);
      box-sizing: border-box;
    }
    /* Перебиваем Form_Wrap: иначе Form_Title — тот же #093333 на тёмном фоне */
    #contacts .feniks-form-header [class*='Form_Title'],
    .feniks-lead-root .feniks-form-header [class*='Form_Title'] {
      color: #eee9e3 !important;
    }
    #contacts .feniks-form-header .feniks-form-lead,
    .feniks-lead-root .feniks-form-header .feniks-form-lead {
      margin: 10px auto 0;
      max-width: 520px;
      font-size: clamp(14px, 1.5vw, 16px);
      line-height: 1.5;
      color: rgba(238, 233, 227, 0.92) !important;
      font-weight: 500;
    }
    #contacts .feniks-form-shell, .feniks-lead-root .feniks-form-shell {
      max-width: 760px;
      margin: 0 auto;
      background: linear-gradient(165deg, #f7f2eb 0%, #eee9e3 42%, #e8e0d6 100%);
      border: 1px solid rgba(9, 51, 51, 0.18);
      border-radius: 20px;
      padding: clamp(18px, 2.2vw, 28px);
      box-shadow:
        0 18px 40px rgba(9, 51, 51, 0.14),
        inset 0 1px 0 rgba(255, 255, 255, 0.65);
    }
    #contacts .feniks-form-feedback, .feniks-lead-root .feniks-form-feedback {
      margin-bottom: 16px;
      border-radius: 14px;
      padding: 14px 16px;
      font-size: 14px;
      line-height: 1.45;
      box-sizing: border-box;
      animation: feniksFormFeedbackIn 0.38s ease;
    }
    @keyframes feniksFormFeedbackIn {
      from {
        opacity: 0;
        transform: translateY(-8px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    #contacts .feniks-form-feedback--hidden, .feniks-lead-root .feniks-form-feedback--hidden {
      display: none !important;
    }
    #contacts .feniks-form-feedback--success, .feniks-lead-root .feniks-form-feedback--success {
      background: rgba(9, 51, 51, 0.07);
      border: 1px solid rgba(9, 51, 51, 0.22);
      color: #093333;
    }
    #contacts .feniks-form-feedback--error, .feniks-lead-root .feniks-form-feedback--error {
      background: rgba(139, 40, 40, 0.07);
      border: 1px solid rgba(139, 40, 40, 0.32);
      color: #5a2222;
    }
    #contacts .feniks-form-feedback-inner, .feniks-lead-root .feniks-form-feedback-inner {
      display: flex;
      align-items: flex-start;
      gap: 12px;
    }
    #contacts .feniks-form-feedback-icon, .feniks-lead-root .feniks-form-feedback-icon {
      flex: 0 0 auto;
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(9, 51, 51, 0.12);
      color: #093333;
    }
    #contacts .feniks-form-feedback--success .feniks-form-feedback-icon, .feniks-lead-root .feniks-form-feedback--success .feniks-form-feedback-icon {
      background: rgba(9, 51, 51, 0.16);
    }
    #contacts .feniks-form-feedback-text strong, .feniks-lead-root .feniks-form-feedback-text strong {
      display: block;
      font-family: 'Philosopher', Candara, 'Segoe UI', Arial, sans-serif;
      font-size: 17px;
      font-weight: 700;
      margin-bottom: 4px;
      letter-spacing: 0.01em;
    }
    #contacts .feniks-form-feedback-text p, .feniks-lead-root .feniks-form-feedback-text p {
      margin: 0;
      opacity: 0.92;
      font-size: 14px;
    }
    #contacts .feniks-form-feedback-dismiss, .feniks-lead-root .feniks-form-feedback-dismiss {
      margin-top: 12px;
      padding: 8px 14px;
      border-radius: 10px;
      border: 1px solid rgba(9, 51, 51, 0.28);
      background: rgba(255, 255, 255, 0.55);
      color: #093333;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      font-family: inherit;
    }
    #contacts .feniks-form-feedback-dismiss:hover, .feniks-lead-root .feniks-form-feedback-dismiss:hover {
      background: rgba(255, 255, 255, 0.85);
    }
    #contacts .feniks-form-grid, .feniks-lead-root .feniks-form-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 12px;
    }
    #contacts .feniks-form-field, .feniks-lead-root .feniks-form-field {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
    #contacts .feniks-form-field label, .feniks-lead-root .feniks-form-field label {
      font-size: 14px;
      font-weight: 700;
      color: #093333;
      letter-spacing: 0.01em;
    }
    #contacts .feniks-form-hint, .feniks-lead-root .feniks-form-hint {
      display: block;
      margin: -2px 0 2px;
      font-size: 12px;
      font-weight: 500;
      line-height: 1.35;
      color: rgba(32, 32, 31, 0.62);
    }
    #contacts .feniks-form-field input, .feniks-lead-root .feniks-form-field input {
      min-height: 48px;
      border-radius: 12px;
      border: 1px solid rgba(9, 51, 51, 0.22);
      background: #fff;
      color: #20201f;
      padding: 12px 14px;
      font-size: 16px;
      line-height: 1.25;
      box-sizing: border-box;
      transition:
        border-color 0.2s ease,
        box-shadow 0.2s ease;
    }
    #contacts .feniks-form-field input:focus, .feniks-lead-root .feniks-form-field input:focus {
      outline: none;
      border-color: #093333;
      box-shadow: 0 0 0 3px rgba(9, 51, 51, 0.12);
    }
    #contacts .feniks-form-field.feniks-form-full, .feniks-lead-root .feniks-form-field.feniks-form-full {
      grid-column: 1 / -1;
    }
    #contacts .feniks-form-note, .feniks-lead-root .feniks-form-note {
      margin-top: 12px;
      font-size: 12px;
      line-height: 1.45;
      color: rgba(32, 32, 31, 0.78);
    }
    #contacts .feniks-form-submit, .feniks-lead-root .feniks-form-submit {
      margin-top: 16px;
      width: 100%;
      min-height: 52px;
      border-radius: 14px;
      border: 1px solid #093333;
      background: linear-gradient(180deg, #0c3d3d 0%, #093333 100%);
      color: #eee9e3;
      font-size: 15px;
      font-weight: 700;
      cursor: pointer;
      font-family: 'Philosopher', Candara, 'Segoe UI', Arial, sans-serif;
      letter-spacing: 0.02em;
      box-shadow: 0 8px 20px rgba(9, 51, 51, 0.25);
      transition:
        transform 0.15s ease,
        box-shadow 0.2s ease,
        opacity 0.2s ease;
    }
    #contacts .feniks-form-submit:hover:not(:disabled), .feniks-lead-root .feniks-form-submit:hover:not(:disabled) {
      background: linear-gradient(180deg, #104747 0%, #0b3a3a 100%);
      box-shadow: 0 10px 26px rgba(9, 51, 51, 0.3);
      transform: translateY(-1px);
    }
    #contacts .feniks-form-submit:active:not(:disabled), .feniks-lead-root .feniks-form-submit:active:not(:disabled) {
      transform: translateY(0);
    }
    #contacts .feniks-form-submit:disabled, .feniks-lead-root .feniks-form-submit:disabled {
      opacity: 0.72;
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
    #contacts .feniks-form-submit.is-loading, .feniks-lead-root .feniks-form-submit.is-loading {
      cursor: wait;
    }
    @media (max-width: 743px) {
      #contacts [class*="Form_Inner"] {
        padding-inline: 12px !important;
      }
      #contacts .b24-form-content {
        padding: 14px !important;
      }
      #contacts .b24-form-control {
        min-height: 48px !important;
        height: 48px !important;
        padding: 12px !important;
      }
      #contacts .b24-form-btn {
        min-height: 48px !important;
      }
      #contacts .feniks-form-shell, .feniks-lead-root .feniks-form-shell {
        border-radius: 16px;
        padding: 16px;
      }
      #contacts .feniks-form-feedback-inner, .feniks-lead-root .feniks-form-feedback-inner {
        flex-direction: column;
        align-items: stretch;
        gap: 10px;
      }
      #contacts .feniks-form-feedback-icon, .feniks-lead-root .feniks-form-feedback-icon {
        align-self: center;
      }
      #contacts .feniks-form-grid, .feniks-lead-root .feniks-form-grid {
        grid-template-columns: 1fr;
        gap: 10px;
      }
      #contacts .feniks-form-submit, .feniks-lead-root .feniks-form-submit {
        min-height: 48px;
      }
    }

    [class*="MarqueeBlock_Item"] {
      color: var(--feniks-green) !important;
    }

    [class*="MarqueeBlock_White"] {
      color: transparent !important;
      -webkit-text-fill-color: transparent !important;
      -webkit-text-stroke: 1px var(--feniks-beige) !important;
      text-stroke: 1px var(--feniks-beige) !important;
    }

    /* Слайдер «Специальные предложения»: тот же каркас, свои тексты, блок деталей всегда открыт */
    [class*="SpecialOffer_Wrapper"] [class*="Card_Info__1N_jt"] {
      height: auto !important;
      overflow: visible !important;
      margin-top: 22px !important;
      border-top-color: rgba(9, 51, 51, 0.12) !important;
    }
    [class*="SpecialOffer_Wrapper"] [class*="Card_ShowMore__ORzix"] {
      display: none !important;
    }
    [class*="SpecialOffer_Wrapper"] .feniks-so-pct {
      color: var(--feniks-green) !important;
      font-size: clamp(20px, 2.6vw, 34px) !important;
      line-height: 1.12 !important;
      white-space: normal !important;
      max-width: min(200px, 46vw);
    }
    [class*="SpecialOffer_Wrapper"] [class*="Card_MainSection"] [class*="Card_Text"] {
      font-size: 15px !important;
      line-height: 1.4 !important;
    }
    [class*="SpecialOffer_Wrapper"] [class*="Card_Row"] [class*="Card_Text"] {
      font-size: 14px !important;
      line-height: 1.38 !important;
    }

    /* Калькулятор: крупнее ручки слайдера — удобнее трогать на телефоне */
    [class*="Calculator_"] .rc-slider-handle {
      width: 22px !important;
      height: 22px !important;
      margin-top: -7px !important;
      border: 2px solid #fff !important;
      box-shadow: 0 2px 8px rgba(9, 51, 51, 0.25) !important;
    }

    /* Только формат «Стандарт» — переключатель Остров/Стандарт скрыт */
    [class*="Calculator_Block"][class*="Calculator_Format"],
    [class*="Calculator_Calculator"] [class*="FormatButtons_Buttons"] {
      display: none !important;
    }

    /* Секция калькулятора: фон бренда и адаптация под тёмный фон */
    #calculator[class*="Calculator_Wrapper"],
    [class*="Calculator_Wrapper"] {
      background: #093333 !important;
      padding-top: clamp(32px, 5vw, 52px) !important;
      padding-bottom: clamp(40px, 6vw, 64px) !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_Calculator"] {
      padding-bottom: 8px !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_Block"]:not([class*="Calculator_Format"]) {
      margin-bottom: 10px !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_Block"] [class*="CheckNumber_Wrapper"],
    [class*="Calculator_Wrapper"] [class*="Calculator_Block"] [class*="RentPrice_Wrapper"] {
      margin-top: 10px !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_Title"] {
      color: #eee9e3 !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_SubTitle"] {
      color: #e4c49e !important;
      line-height: 1.4 !important;
      max-width: 40rem !important;
      font-size: clamp(15px, 2.1vw, 18px) !important;
      font-weight: 600 !important;
    }
    [class*="Calculator_Wrapper"] [class*="Calculator_SubTitle"] span {
      color: #e4c49e !important;
    }
    [class*="Calculator_Wrapper"] .feniks-calc-subhint {
      display: block !important;
      margin-top: 6px !important;
      font-size: 14px !important;
      font-weight: 500 !important;
      color: rgba(238, 233, 227, 0.88) !important;
      line-height: 1.35 !important;
    }
    [class*="Calculator_Wrapper"] [class*="CheckNumber_Value"],
    [class*="Calculator_Wrapper"] [class*="RentPrice_Value"] {
      color: #eee9e3 !important;
      font-size: clamp(16px, 2.4vw, 20px) !important;
      font-weight: 600 !important;
      line-height: 1.3 !important;
      margin-top: 8px !important;
    }
    [class*="Calculator_Wrapper"] .rc-slider-rail {
      background: rgba(238, 233, 227, 0.2) !important;
    }
    [class*="Calculator_Wrapper"] .rc-slider-track {
      background: #e4c49e !important;
    }
    [class*="Calculator_Wrapper"] .rc-slider-handle {
      background: #e4c49e !important;
      border: 2px solid #fff !important;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.35) !important;
    }
    /* Итоги: только текст — подпись, ниже цифра; без фонов и карточки */
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Card"],
    [class*="Calculator_Wrapper"] [class*="CalculateCard"] {
      background: transparent !important;
      border: none !important;
      box-shadow: none !important;
      border-radius: 0 !important;
      padding: 4px 0 0 !important;
      margin: 0 !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Blocks"] {
      display: flex !important;
      flex-direction: column !important;
      align-items: flex-start !important;
      gap: 0 !important;
      margin: 0 0 14px !important;
      padding: 0 !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Block"] {
      display: flex !important;
      flex-direction: column !important;
      align-items: flex-start !important;
      gap: 2px !important;
      width: 100% !important;
      padding: 0 0 14px !important;
      margin: 0 !important;
      text-align: left !important;
      border: none !important;
      background: transparent !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Title"] {
      color: rgba(238, 233, 227, 0.78) !important;
      font-size: 13px !important;
      font-weight: 500 !important;
      line-height: 1.3 !important;
      margin: 0 !important;
      max-width: none !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Value"] {
      color: #eee9e3 !important;
      font-size: clamp(20px, 4.2vw, 28px) !important;
      font-weight: 700 !important;
      line-height: 1.2 !important;
      margin: 0 !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Button"] {
      width: auto !important;
      align-self: flex-start !important;
      background: transparent !important;
      border: 1px solid rgba(228, 196, 158, 0.45) !important;
      border-radius: 10px !important;
      box-shadow: none !important;
      padding: 9px 14px !important;
      margin-top: 2px !important;
    }
    [class*="Calculator_Wrapper"] [class*="CalculateCard_Button"] span {
      color: #e4c49e !important;
    }

    [class*="Steps_Cup"] img {
      width: auto !important;
      max-width: min(100%, 240px) !important;
      height: auto !important;
      max-height: min(52vh, 320px) !important;
      object-fit: contain !important;
    }

    /* Убираем блок App Store / Google Play / соцсети One Price из футера */
    [class*="DesktopFooter_AppsAndSocials"],
    [class*="MobileFooter_AppsAndSocials"],
    [class*="TabletMobileFooter_Apps"],
    [class*="TabletMobileFooter_Bottom__"],
    [class*="TabletMobileFooter_License__"],
    [class*="TabletMobileFooter_Socials__"] {
      display: none !important;
    }

    /* Нижняя полоса: копирайт OPC, оферта, Digital Lab */
    [class*="BottomFooter_Wrapper"] {
      display: none !important;
    }

    [class*="DesktopFooter_Logo__"],
    [class*="TabletMobileFooter_Logo__"] {
      width: 112px !important;
      height: auto !important;
      max-height: 76px !important;
      object-fit: contain !important;
    }
    /*
      Legacy: DesktopFooter_Menu = grid (4 колонки под 4 <ul>) — один <ul> попадал в колонку ~137px.
      У MenuItem_Item глобально margin-bottom: 14px под вертикальные списки.
    */
    [class*="DesktopFooter_Footer"] [class*="DesktopFooter_Menu__"] {
      display: flex !important;
      flex-direction: row !important;
      flex-wrap: nowrap !important;
      align-items: center !important;
      grid-template-columns: none !important;
      gap: 0 !important;
      flex: 1 1 auto !important;
      min-width: min-content !important;
      overflow-x: visible !important;
      overflow: visible !important;
      -webkit-overflow-scrolling: touch;
    }
    /* Не трогаем display у TabletMobileFooter_Menu: на 744–1024px legacy ставит display:none,
       иначе !important перебивает скрытие и телефон «теряется» внутри невидимого блока. */
    [class*="DesktopFooter_Menu__"] .feniks-footer-nav-ul,
    [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"]) .feniks-footer-nav-ul {
      display: flex !important;
      flex-direction: row !important;
      flex-wrap: nowrap !important;
      align-items: center !important;
      gap: 8px 26px !important;
      list-style: none !important;
      margin: 0 !important;
      padding: 0 !important;
      width: max-content !important;
      max-width: none !important;
    }
    [class*="DesktopFooter_Menu__"] [class*="DesktopFooter_MenuItem"].feniks-footer-nav-ul,
    [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"])
      [class*="TabletMobileFooter_MenuItem"].feniks-footer-nav-ul {
      width: auto !important;
      max-width: none !important;
    }
    [class*="DesktopFooter_Menu__"] .feniks-footer-nav-ul > li,
    [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"]) .feniks-footer-nav-ul > li,
    [class*="DesktopFooter_Menu__"] .feniks-footer-nav-ul > li:not(:last-child),
    [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"]) .feniks-footer-nav-ul > li:not(:last-child) {
      flex-shrink: 0 !important;
      margin-bottom: 0 !important;
      margin-top: 0 !important;
    }
    [class*="DesktopFooter_Menu__"] .feniks-footer-nav-ul a,
    [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"]) .feniks-footer-nav-ul a {
      white-space: nowrap !important;
    }
    [class*="DesktopFooter_Info"] > a[class*="PhoneLink"],
    [class*="TabletMobileFooter_Info"] > a[class*="PhoneLink"] {
      color: #fff !important;
      white-space: nowrap !important;
      font-size: 21px !important;
      font-weight: 500 !important;
      text-decoration: none !important;
    }

    [class*="Footer_Wrap"], footer {
      background: var(--feniks-green) !important;
      color: var(--feniks-ivory) !important;
    }

    [class*="Footer_Wrap"] a, footer a {
      color: var(--feniks-beige) !important;
    }

    [class*="Line__"], [class*="Thread__"] svg path {
      stroke: var(--feniks-green) !important;
    }
    /* Временно отключено по запросу */
    [class*="SuccessStories_Wrapper"] {
      display: none !important;
      visibility: hidden !important;
      opacity: 0 !important;
      pointer-events: none !important;
      height: 0 !important;
      overflow: hidden !important;
    }
  `

  doc
    .querySelectorAll('.ThemeSelector_Wrapper__f7vGu, [class*="ThemeSelector_Wrapper"]')
    .forEach((el) => el.remove())

  doc
    .querySelectorAll('.TitleScreen_WrapLogo__Yg0HI, [class*="TitleScreen_WrapLogo"]')
    .forEach((el) => el.remove())

  /* Нижний OPC: BottomFooter, кнопка «вверх», блок магазинов; из DOM, не только display:none */
  const removePostFooterChrome = () => {
    doc.querySelectorAll('[class*="BottomFooter_Wrapper"]').forEach((el) => el.remove())
    doc.querySelectorAll('[class*="ButtonUp_"]').forEach((el) => el.remove())
    doc.querySelectorAll('[class*="DesktopFooter_AppsAndSocials"], [class*="MobileFooter_AppsAndSocials"]').forEach(
      (el) => el.remove(),
    )
  }

  const removeLegacySiteHeader = () => {
    doc
      .querySelectorAll('[class*="Header_Wrapper"], [data-container="header-white"]')
      .forEach((el) => el.remove())
  }
  const removeSuccessStoriesBlock = () => {
    doc.querySelectorAll('[class*="SuccessStories_Wrapper"]').forEach((el) => el.remove())
  }

  removeLegacySiteHeader()
  setTimeout(removeLegacySiteHeader, 500)
  setTimeout(removeLegacySiteHeader, 2000)
  removeSuccessStoriesBlock()
  setTimeout(removeSuccessStoriesBlock, 500)
  setTimeout(removeSuccessStoriesBlock, 2000)

  removePostFooterChrome()
  setTimeout(removePostFooterChrome, 500)
  setTimeout(removePostFooterChrome, 2000)

  const heroLogo = doc.querySelector('.TitleScreen_Logo__2mxVl img')
  if (heroLogo) {
    heroLogo.setAttribute('src', '/legacy/icon/logophenix.svg')
    heroLogo.setAttribute('srcset', '')
  }

  const franchiseLogo = doc.querySelector('.Franchise_ImageLogo__bwWWt img')
  if (franchiseLogo) {
    franchiseLogo.setAttribute('src', '/legacy/icon/logophenix.svg')
    franchiseLogo.setAttribute('srcset', '')
  }

  const patchStepsCupImage = () => {
    const cupSrc = '/legacy/images/12/coffeicom.png'
    doc.querySelectorAll('[class*="Steps_Cup"] img').forEach((img) => {
      img.removeAttribute('srcset')
      img.removeAttribute('sizes')
      img.setAttribute('src', cupSrc)
      img.setAttribute('alt', 'Стакан «Кофе Феникс»')
      img.removeAttribute('width')
      img.removeAttribute('height')
    })
  }

  const setupStepsCupImageObserver = () => {
    const win = doc.defaultView
    if (!win || !doc.body || doc.body.dataset.feniksStepsCupObs) return
    doc.body.dataset.feniksStepsCupObs = '1'
    let raf = 0
    const obs = new MutationObserver(() => {
      if (raf) win.cancelAnimationFrame(raf)
      raf = win.requestAnimationFrame(() => {
        raf = 0
        patchStepsCupImage()
      })
    })
    obs.observe(doc.body, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['src', 'srcset'],
    })
  }

  patchStepsCupImage()
  setTimeout(patchStepsCupImage, 400)
  setTimeout(patchStepsCupImage, 1600)
  setupStepsCupImageObserver()

  const franchiseLeft = doc.querySelector(
    '.Franchise_LeftContentBlock__YKxml, [class*="Franchise_LeftContentBlock"]',
  )
  if (franchiseLeft) {
    const profitIntro = franchiseLeft.querySelector('[class*="Franchise_Text__TpL7Z"]')
    const profitNote = franchiseLeft.querySelector('[class*="Franchise_SmallText__8RbIO"]')
    if (profitIntro) {
      profitIntro.innerHTML =
        'Получай от&nbsp;<span>420&nbsp;000&nbsp;рублей\u2009в&nbsp;месяц</span> по&nbsp;проверенной бизнес-модели*'
    }
    if (profitNote) {
      profitNote.textContent =
        '*Возврат инвестиций от 12 месяцев с одной кофейни при вложении от 5 000 000 рублей'
    }
  }

  const heroDescription = doc.querySelector('.TitleScreen_Description__3BEJx')
  if (heroDescription) {
    heroDescription.innerHTML = `
      <div class="feniks-hero-grid">
        <div class="feniks-hero-copy">
          <span class="feniks-hero-title">СТАНЬТЕ ЧАСТЬЮ</span>
          <span class="feniks-hero-line">успешной сети кофеен</span>
          <span class="feniks-hero-brand">«Кофе Феникс»</span>
          <span class="feniks-hero-subline">и партнером группы компаний</span>
          <span class="feniks-hero-brand">«Вилла-Феникс»</span>
        </div>
        <div class="feniks-hero-side-logo">
          <img src="/legacy/icon/logophenix.svg" alt="Кофе Феникс" />
        </div>
        <span class="feniks-hero-note">Масштабируйте бизнес надежно и со вкусом</span>
      </div>
    `
  }

  // Align key section copy with commercial proposal wording.
  setText(
    ['[class*="About_Title"]', '[class*="History_Title"]'],
    'История бренда',
  )
  setText(
    ['[class*="Franchise_Title"]', '[class*="Advantages_Title"]'],
    'Преимущества бренда',
  )
  setText(
    ['[class*="Conditions_Title"]', '[class*="Benefits_Title"]'],
    'Преимущества франшизы',
  )
  setText(
    ['[class*="Calculator_Title"]', '[class*="Economy_Title"]'],
    'Экономика проекта',
  )
  setText(
    ['[class*="Formats_Title"]', '[class*="Package_Title"]'],
    'Варианты сотрудничества',
  )

  const kpArrow =
    '<img alt="" class="Card_ArrowIcon__NCIKQ" decoding="async" height="44" loading="lazy" src="/legacy/images/arrow-down-icon.185e98ed.svg" width="22" style="color:transparent"/>'

  const conditionsSub = doc.querySelector('[class*="Conditions_SubTitle"]')
  if (conditionsSub) {
    conditionsSub.innerHTML = 'Пакеты <span>«ПАРТНЕРС» и «VIP под ключ»</span>'
  }

  const conditionsCards = doc.querySelector('[class*="Conditions_Cards"]')
  if (conditionsCards) {
    conditionsCards.innerHTML = `
<div class="Card_Card__llLUt Card_White__kNkoT Card_Standart__eSNEI">
  <p class="Card_Label__tpuD_">Пакет «Партнерс»</p>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Price__KW5XX">500 000 ₽${kpArrow}</p>
    <p class="Card_Text__yf6Vd">Паушальный взнос — единовременный платеж за право входа в сеть.</p>
  </div>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Price__KW5XX">5%${kpArrow}</p>
    <p class="Card_Text__yf6Vd">Роялти: 5% от ежемесячной выручки (регулярный платеж за использование бренда и поддержку).</p>
  </div>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Text__yf6Vd feniks-kp-intro">Оптимальный вариант для тех, кто хочет инвестировать в готовую бизнес-модель и запустить точку самостоятельно. Входите в прибыльный бизнес с готовой системой и командой экспертов.</p>
  </div>
  <details class="feniks-kp-details">
    <summary>Полный состав пакета (10 пунктов)</summary>
    <ol class="feniks-conditions-list">
      <li><strong>Право бренда «Феникс».</strong> Торговая марка, логотип, фирменный стиль, узнаваемое имя для гостей.</li>
      <li><strong>Документация и стандарты.</strong> Брендбук, операционные руководства (техкарты, сервис, команда, санитария), маркетинговые шаблоны.</li>
      <li><strong>Локация.</strong> Поиск и оценка помещений, трафик, конкурентная среда.</li>
      <li><strong>Дизайн и зонирование.</strong> Типовой интерьер в стиле «Феникс», расстановка оборудования и рабочих зон.</li>
      <li><strong>Обучение.</strong> Франчайзи и команда, бариста, стажировка в действующей кофейне сети.</li>
      <li><strong>Поставщики и цены.</strong> Оборудование, сырьё (зерно, молоко, сиропы, выпечка) по условиям сети.</li>
      <li><strong>Маркетинг.</strong> Материалы и макеты, консультации по локальному продвижению и первым гостям.</li>
      <li><strong>IT и ПО.</strong> Кассы, учёт, рекомендации по лояльности.</li>
      <li><strong>Сопровождение.</strong> Открытие (юрлицо, разрешения), регулярные консультации по операционке, маркетингу и управлению, поддержка по телефону.</li>
      <li><strong>Открытие.</strong> Рекомендации и план торжественного запуска кофейни.</li>
    </ol>
  </details>
</div>
<div class="Card_Card__llLUt Card_Green__BKOuy Card_Standart__eSNEI">
  <p class="Card_Label__tpuD_">Пакет «VIP ПОД КЛЮЧ»</p>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Price__KW5XX">5 000 000 ₽${kpArrow}</p>
    <p class="Card_Text__yf6Vd">Паушальный взнос — единовременный платеж, покрывающий значительную часть пред-открытия.</p>
  </div>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Price__KW5XX">5%${kpArrow}</p>
    <p class="Card_Text__yf6Vd">Роялти: 5% от ежемесячной выручки (регулярный платеж за использование бренда и поддержку).</p>
  </div>
  <div class="Card_Section__Ilgtw">
    <p class="Card_Text__yf6Vd feniks-kp-intro">Задача франчайзи — контроль и управление готовым бизнесом; открытие и запуск берёт на себя лицензиар.</p>
  </div>
  <details class="feniks-kp-details">
    <summary>Полный состав пакета (9 пунктов)</summary>
    <ol class="feniks-conditions-list">
      <li><strong>Юридическое сопровождение.</strong> Регистрация юрлица/ИП, разрешения и лицензии для общепита «под ключ».</li>
      <li><strong>Локация «под ключ».</strong> Подбор помещения, переговоры с арендодателем, ремонт и отделка по дизайн-проекту.</li>
      <li><strong>Дизайн и брендирование.</strong> Индивидуальный проект в брендбуке «Феникс», вывески, наружная и внутренняя айдентика.</li>
      <li><strong>Оборудование и мебель.</strong> Подбор, закупка, доставка и монтаж кофейного оборудования и мебели зала и бара.</li>
      <li><strong>Запуск ассортимента.</strong> Первое наполнение (кофе, молоко, сиропы, выпечка, снэки) и расходники для работы.</li>
      <li><strong>Команда «под ключ».</strong> Подбор и обучение бариста в сети или на месте, стандарты сервиса.</li>
      <li><strong>Маркетинг и запуск.</strong> План для локации, реклама и SMM, поддержка на торжественном открытии.</li>
      <li><strong>Лояльность.</strong> Настройка и интеграция программы лояльности «Феникс» в учёт.</li>
      <li><strong>Поддержка.</strong> Персональный менеджер, аудиты качества, обновление техкарт и меню, база знаний сети.</li>
    </ol>
  </details>
</div>
`.trim()
  }

  const conditionsWrapper = doc.querySelector('[class*="Conditions_Wrapper"]')
  if (conditionsWrapper) conditionsWrapper.id = 'package'

  const specialOfferSub = doc.querySelector('[class*="SpecialOffer_SubTitle"]')
  if (specialOfferSub) {
    specialOfferSub.innerHTML = 'Актуальность <span>рынка и бренда</span>'
  }

  const specialOfferWrapper = doc.querySelector('[class*="SpecialOffer_Wrapper"]')
  if (specialOfferWrapper) {
    specialOfferWrapper.id = 'market'

    const soRow = (text) =>
      `<div class="Card_Row__K2MSz"><div class="Card_CoffeeIcon__0VZbY"></div><p class="Card_Text__wDrpn">${text}</p></div>`

    const slideContents = [
      `
<p class="Card_Label__npIKR Card_Standart__9t04v">Актуальность рынка</p>
<div class="Card_MainSection__Pu1ZE">
  <div class="Card_Prices__DaVf6">
    <p class="Card_CurrentPrice__Al4Vm Card_Standart__9t04v feniks-so-pct">2026</p>
  </div>
  <p class="Card_Text__wDrpn">Рынок Москвы 2026 года — это рынок эмоциональной привязанности: гость выбирает бренд, атмосферу и доверие, а не только чек.</p>
</div>
<div class="Card_SubSection__GWr1m">
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Сеть</p>
    <p class="Card_Text__wDrpn">«Кофе Феникс» — часть группы компаний «Вилла-Феникс» и сильного бренда.</p>
  </div>
  <div class="Card_Separator__lPlfR"></div>
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Выгода</p>
    <p class="Card_Text__wDrpn">Вы заходите в нишу с конкурентным средним чеком и лояльной аудиторией.</p>
  </div>
</div>
<div class="Card_Info__1N_jt feniks-so-info">
  <div class="Card_Rows__5DrwE">
    ${soRow(
      'В условиях инфляции и налогового давления выживают проекты, которые дают не только продукт, но и интеграцию в образ жизни клиента и стабильное качество, защищённое сильным брендом группы.',
    )}
    ${soRow(
      '«Кофе Феникс» как часть группы — это не просто бизнес, а стратегически верное решение в текущих рыночных условиях.',
    )}
    ${soRow(
      'Ваша выгода — понятная экономика точки, управляемость процессов и опора на стандарты сети.',
    )}
    ${soRow(
      'Фокус на удержании и повторных визитах снижает зависимость от агрессивных промо и повышает устойчивость модели.',
    )}
  </div>
</div>
<button type="button" class="Card_ShowMore__ORzix Card_Standart__9t04v feniks-so-hide-btn"><span>Показать детали</span></button>
`.trim(),
      `
<p class="Card_Label__npIKR Card_Standart__9t04v">Преимущества бренда</p>
<div class="Card_MainSection__Pu1ZE">
  <div class="Card_Prices__DaVf6">
    <p class="Card_CurrentPrice__Al4Vm Card_Standart__9t04v feniks-so-pct">Качество</p>
  </div>
  <p class="Card_Text__wDrpn">Исключительные стандарты качества и внимание к деталям в обслуживании формируют фундамент нашего гостеприимства.</p>
</div>
<div class="Card_SubSection__GWr1m">
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Миссия</p>
    <p class="Card_Text__wDrpn">Формирование культуры качественного сервиса и досуга.</p>
  </div>
  <div class="Card_Separator__lPlfR"></div>
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Фокус</p>
    <p class="Card_Text__wDrpn">Единый стандарт сервиса и удобство для резидентов и гостей «Вилла-Феникс» и «Кофе Феникс».</p>
  </div>
</div>
<div class="Card_Info__1N_jt feniks-so-info">
  <div class="Card_Rows__5DrwE">
    ${soRow(
      'Мы создаём эталонный продукт, который становится смысловым центром для общения и объединения гостей.',
    )}
    ${soRow(
      'Команда выстраивает сервис как систему: от приветствия до финальной ноты вкуса напитка.',
    )}
    ${soRow(
      'Бренд объединяет кофейню, отельную среду и гастрономию — для гостя это цельный опыт «Феникс».',
    )}
  </div>
</div>
<button type="button" class="Card_ShowMore__ORzix Card_Standart__9t04v feniks-so-hide-btn"><span>Показать детали</span></button>
`.trim(),
      `
<p class="Card_Label__npIKR Card_Standart__9t04v">Преимущества франшизы</p>
<div class="Card_MainSection__Pu1ZE">
  <div class="Card_Prices__DaVf6">
    <p class="Card_CurrentPrice__Al4Vm Card_Standart__9t04v feniks-so-pct">4 опоры</p>
  </div>
  <p class="Card_Text__wDrpn">Готовая система запуска и сопровождения — вы развиваете бизнес на проверенных процессах сети.</p>
</div>
<div class="Card_SubSection__GWr1m">
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Старт</p>
    <p class="Card_Text__wDrpn">Прозрачные шаги открытия, обучение и контрольные списки качества.</p>
  </div>
  <div class="Card_Separator__lPlfR"></div>
  <div class="Card_Section__H4sVL">
    <p class="Card_Percent__eW_Ls Card_Standart__9t04v feniks-so-pct">Рост</p>
    <p class="Card_Text__wDrpn">Маркетинговые материалы и методики удержания гостя — с первого дня работы точки.</p>
  </div>
</div>
<div class="Card_Info__1N_jt feniks-so-info">
  <div class="Card_Rows__5DrwE">
    ${soRow(
      'Проверенная бизнес-модель: мы уже прошли путь ошибок и создали работающую систему.',
    )}
    ${soRow(
      'Пул поставщиков: готовая база надёжных производителей с эксклюзивными условиями для партнёров сети.',
    )}
    ${soRow(
      'Маркетинговая поддержка: готовые инструменты для привлечения покупателей с первого дня.',
    )}
    ${soRow(
      'Стандарты качества: чёткие чек-листы по сервису, хранению и мерчандайзингу.',
    )}
  </div>
</div>
<button type="button" class="Card_ShowMore__ORzix Card_Standart__9t04v feniks-so-hide-btn"><span>Показать детали</span></button>
`.trim(),
    ]

    const offerSlides = specialOfferWrapper.querySelectorAll('.swiper-slide')
    offerSlides.forEach((slide, index) => {
      const html = slideContents[index]
      if (!html) return
      const content = slide.querySelector('[class*="Card_Content"]')
      if (content) content.innerHTML = html
    })

    const firstOfferImg = offerSlides[0]?.querySelector('[class*="Card_Image"] img')
    if (firstOfferImg) {
      firstOfferImg.setAttribute('src', '/legacy/images/12/455A0085.jpg')
      firstOfferImg.setAttribute('alt', 'Интерьер «Кофе Феникс»')
    }

    const secondOfferImg = offerSlides[1]?.querySelector('[class*="Card_Image"] img')
    if (secondOfferImg) {
      secondOfferImg.setAttribute('src', '/legacy/images/12/455A0137.jpg')
      secondOfferImg.setAttribute('alt', 'Зал «Кофе Феникс»')
    }

    const thirdOfferImg = offerSlides[2]?.querySelector('[class*="Card_Image"] img')
    if (thirdOfferImg) {
      thirdOfferImg.setAttribute('src', '/legacy/images/12/455A0099.jpg')
      thirdOfferImg.setAttribute('alt', 'Зона бренда «Кофе Феникс»')
    }

    requestAnimationFrame(() => {
      const swEl = specialOfferWrapper.querySelector('.swiper')
      const inst = swEl?.swiper
      if (inst && typeof inst.update === 'function') inst.update()
    })
  }

  const forceCalculatorStandardFormat = () => {
    const wrap = doc.querySelector('[class*="FormatButtons_Buttons"]')
    if (!wrap) return
    const standardBtn = Array.from(wrap.querySelectorAll('button')).find(
      (b) => b.querySelector('span')?.textContent?.trim() === 'Стандарт',
    )
    if (!standardBtn || standardBtn.className.includes('Active')) return
    standardBtn.click()
  }

  const patchCalculatorHints = () => {
    forceCalculatorStandardFormat()
    const subs = doc.querySelectorAll('[class*="Calculator_SubTitle"]')
    const html = [
      '',
      '<span>1.</span> Чеков в день<span class="feniks-calc-subhint">Нагрузка точки и объём продаж</span>',
      '<span>2.</span> Аренда в месяц<span class="feniks-calc-subhint">Постоянный операционный расход</span>',
    ]
    subs.forEach((el, i) => {
      if (html[i]) el.innerHTML = html[i]
    })
  }

  const setupRentPriceDisplayFix = () => {
    const wrap = doc.querySelector('[class*="RentPrice_Wrapper"]')
    if (!wrap || wrap.dataset.feniksRentFix) return
    wrap.dataset.feniksRentFix = '1'
    const sync = () => {
      const slider = wrap.querySelector('[role="slider"]')
      const valEl = wrap.querySelector('[class*="RentPrice_Value"]')
      if (!slider || !valEl) return
      const raw = slider.getAttribute('aria-valuenow')
      const v = parseInt(raw ?? '', 10)
      if (!Number.isFinite(v) || v < 0) return
      valEl.textContent = `${new Intl.NumberFormat('ru-RU').format(v)} ₽ в месяц`
    }
    sync()
    const obs = new MutationObserver(sync)
    obs.observe(wrap, { subtree: true, attributes: true, attributeFilter: ['aria-valuenow'] })
    wrap.querySelectorAll('[role="slider"]').forEach((handle) => {
      obs.observe(handle, { attributes: true, attributeFilter: ['aria-valuenow', 'style'] })
    })
  }

  patchCalculatorHints()
  setTimeout(patchCalculatorHints, 400)
  setTimeout(patchCalculatorHints, 1600)
  setTimeout(forceCalculatorStandardFormat, 80)
  setTimeout(forceCalculatorStandardFormat, 900)
  setupRentPriceDisplayFix()
  setTimeout(setupRentPriceDisplayFix, 500)

  const patchFormatsCardsText = () => {
    const formatsData = [
      {
        title: 'Пакет «Партнерс»',
        area: '',
        investment: '500 000 рублей',
        items: [
          ['Описание', 'единовременный платеж за право входа в сеть'],
          [
            'Роялти',
            '5% от ежемесячной выручки (регулярный платеж за использование бренда и поддержку)',
          ],
        ],
        sum: '5%',
      },
      {
        title: 'Пакет «VIP ПОД КЛЮЧ»',
        area: '',
        investment: '5 000 000 рублей',
        items: [
          ['Описание', 'единовременный платеж, покрывающий значительную часть пред-открытия'],
          [
            'Роялти',
            '5% от ежемесячной выручки (регулярный платеж за использование бренда и поддержку)',
          ],
        ],
        sum: '5%',
      },
      {
        title: 'Формат Стандарт с посадкой',
        area: '25-40 м²',
        investment: 'От 3 995 000 ₽',
        items: [
          ['Оборудование', '950 000 ₽'],
          ['Мебель', '950 000 ₽'],
          ['Проектирование', '50 000 ₽'],
          ['Реклама', '290 000 ₽'],
          ['CMP', '1 290 000 ₽'],
          ['IT оборудование', '200 000 ₽'],
          ['Прочее', '265 000 ₽'],
        ],
        sum: '600 000 ₽',
      },
    ]

    const slides = Array.from(doc.querySelectorAll('[class*="Formats_Slide"]'))
    if (!slides.length) return

    slides.forEach((slide) => {
      const img = slide.querySelector('[class*="FormatCard_Image"] img')
      const src = img?.getAttribute('src') || ''
      if (src.includes('/81a/')) {
        slide.remove()
        return
      }
      let data = null
      let idx = 0
      if (src.includes('/918/')) {
        data = formatsData[0]
        idx = 0
      } else if (src.includes('/80b/')) {
        data = formatsData[1]
        idx = 1
      } else if (src.includes('/81a/')) {
        data = formatsData[2]
        idx = 2
      }
      if (!data) return

      if (img) {
        if (idx === 0) {
          img.setAttribute('src', '/legacy/images/12/455A0085.jpg')
          img.setAttribute('alt', 'Пакет «Партнерс»')
        } else if (idx === 1) {
          img.setAttribute('src', '/legacy/images/12/455A0077.jpg')
          img.setAttribute('alt', 'Пакет «VIP ПОД КЛЮЧ»')
        }
        img.removeAttribute('srcset')
        img.removeAttribute('sizes')
      }

      const title = slide.querySelector('[class*="FormatCard_Title"]')
      const area = slide.querySelector('[class*="FormatCard_Area"]')
      if (title) title.textContent = data.title
      if (area) area.textContent = data.area

      const caption = slide.querySelector('[class*="FormatCard_Caption"]')
      const capLabel = caption?.querySelector('[class*="FormatCard_Label"]')
      const capValue = caption?.querySelector('[class*="FormatCard_Value"]')
      if (capLabel) capLabel.textContent = 'Инвестиции'
      if (capValue) capValue.textContent = data.investment

      const content = slide.querySelector('[class*="FormatCard_Content"]')
      if (content) {
        content.innerHTML = data.items
          .map(
            ([label, value]) => `
              <div class="FormatCard_Item__wJ3Ib">
                <div class="FormatCard_Label__f88hw">${label}</div>
                <div class="FormatCard_Value__KxBO4">${value}</div>
              </div>
            `,
          )
          .join('')
      }

      const sum = slide.querySelector('[class*="FormatCard_Sum"]')
      const sumLabel = sum?.querySelector('[class*="FormatCard_Label"]')
      const sumValue = sum?.querySelector('[class*="FormatCard_Value"]')
      if (sumLabel) sumLabel.textContent = idx <= 1 ? 'Роялти' : 'Паушальный взнос'
      if (sumValue) sumValue.textContent = data.sum
    })
  }

  patchFormatsCardsText()
  setTimeout(patchFormatsCardsText, 400)
  setTimeout(patchFormatsCardsText, 1600)
  const setupFormatsObserver = () => {
    const wrap = doc.querySelector('[class*="Formats_Wrap"], [class*="Formats_Slider"]')
    const win = doc.defaultView
    if (!wrap || !win || wrap.dataset.feniksFormatsObs) return
    wrap.dataset.feniksFormatsObs = '1'
    let raf = 0
    const obs = new MutationObserver(() => {
      if (raf) win.cancelAnimationFrame(raf)
      raf = win.requestAnimationFrame(() => {
        raf = 0
        patchFormatsCardsText()
      })
    })
    obs.observe(wrap, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['class', 'style', 'src'],
    })
  }
  setupFormatsObserver()

  const secondButtonSpan = doc.querySelector(
    '.TitleScreen_Buttons__0Vevh button:nth-child(2) span',
  )
  if (secondButtonSpan) {
    const icon = secondButtonSpan.querySelector('div')
    secondButtonSpan.textContent = 'Получить финпоказатели'
    if (icon) secondButtonSpan.appendChild(icon)
  }

  setText(
    ['[class*="Footer_Cta"]', '[class*="Join_Title"]'],
    'Присоединяйтесь к команде будущего!',
  )

  // Возвращаем нативный контент "Franchise_Wrap" из legacy-разметки:
  // без принудительной подмены bubble-текстов и без скрытия дополнительных bubble-элементов.

  const FENIKS_FOOTER_PHONE_TEXT = '+7 985 667 87 77'
  const FENIKS_FOOTER_PHONE_HREF = 'tel:+79856678777'
  const FENIKS_FOOTER_LOGO = '/legacy/icon/logophenix.svg'

  const setFeniksAnchorIds = () => {
    const brandEl = doc.querySelector('[class*="TitleScreen_Wrapper"]')
    if (brandEl) brandEl.id = 'brand'
    const pkg = doc.querySelector('[class*="Conditions_Wrapper"]')
    if (pkg) pkg.id = 'package'
    const mkt = doc.querySelector('[class*="SpecialOffer_Wrapper"]')
    if (mkt) mkt.id = 'market'
    const formEl = doc.querySelector('[class*="Form_Wrap"]')
    if (formEl) formEl.id = 'contacts'
    const calcEl = doc.querySelector('[class*="Calculator_Wrapper"]')
    if (calcEl) calcEl.id = 'calculator'
  }

  const stripB24Recaptcha = () => {
    doc.querySelectorAll('.b24-form-recaptcha').forEach((el) => el.remove())
    doc.querySelectorAll('.grecaptcha-badge').forEach((el) => el.remove())
    doc.querySelectorAll('textarea.g-recaptcha-response').forEach((el) => el.remove())
  }

  let feniksLeadMountSeq = 0

  const buildFeniksLeadRootHtml = (uid) => `
      <div class="feniks-lead-root">
      <div class="feniks-form-header">
        <div class="Form_Title__w4lDR">Заявка на франшизу</div>
        <p class="feniks-form-lead">Оставьте контакты — перезвоним и ответим на вопросы по пакету и запуску.</p>
      </div>
      <div class="feniks-form-shell">
        <div class="feniks-form-feedback feniks-form-feedback--hidden" role="status" aria-live="polite"></div>
        <form class="feniks-lead-form" novalidate>
          <div class="feniks-form-grid">
            <div class="feniks-form-field feniks-form-full">
              <label for="feniks-fullname-${uid}">ФИО</label>
              <span class="feniks-form-hint">Введите ФИО</span>
              <input id="feniks-fullname-${uid}" name="fullName" type="text" required placeholder="Иванов Иван Иванович" autocomplete="name" />
            </div>
            <div class="feniks-form-field">
              <label for="feniks-phone-${uid}">Телефон</label>
              <input id="feniks-phone-${uid}" name="phone" type="tel" required placeholder="+7 (___) ___-__-__" autocomplete="tel" />
            </div>
            <div class="feniks-form-field">
              <label for="feniks-email-${uid}">E-mail</label>
              <input id="feniks-email-${uid}" name="email" type="email" required placeholder="example@mail.ru" autocomplete="email" />
            </div>
            <div class="feniks-form-field feniks-form-full">
              <label for="feniks-city-${uid}">Город</label>
              <input id="feniks-city-${uid}" name="city" type="text" required placeholder="Введите город" autocomplete="address-level2" />
            </div>
          </div>
          <div class="feniks-form-note">
            Нажимая «Отправить», вы соглашаетесь на обработку персональных данных.
          </div>
          <button class="feniks-form-submit" type="submit">
            <span class="feniks-form-submit-text">Отправить заявку</span>
          </button>
        </form>
      </div>
      </div>
    `

  const bindFeniksLeadForm = (root) => {
    const form = root.querySelector('.feniks-lead-form')
    const feedbackEl = root.querySelector('.feniks-form-feedback')
    if (!form || !feedbackEl) return
    const successIconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>`
    let feedbackHideTimer

    const hideFeedback = () => {
      if (!feedbackEl) return
      clearTimeout(feedbackHideTimer)
      feedbackEl.classList.add('feniks-form-feedback--hidden')
      feedbackEl.innerHTML = ''
      feedbackEl.removeAttribute('role')
    }

    const showFeedbackSuccess = () => {
      if (!feedbackEl) return
      clearTimeout(feedbackHideTimer)
      feedbackEl.classList.remove('feniks-form-feedback--hidden', 'feniks-form-feedback--error')
      feedbackEl.classList.add('feniks-form-feedback--success')
      feedbackEl.setAttribute('role', 'status')
      feedbackEl.innerHTML = `
        <div class="feniks-form-feedback-inner">
          <div class="feniks-form-feedback-icon">${successIconSvg}</div>
          <div class="feniks-form-feedback-text">
            <strong>Заявка отправлена</strong>
            <p>Спасибо! Мы свяжемся с вами в ближайшее время.</p>
            <button type="button" class="feniks-form-feedback-dismiss">Понятно</button>
          </div>
        </div>
      `
      const dismiss = feedbackEl.querySelector('.feniks-form-feedback-dismiss')
      dismiss?.addEventListener('click', () => hideFeedback())
      feedbackHideTimer = setTimeout(() => hideFeedback(), 8000)
      feedbackEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
    }

    const showFeedbackError = (message) => {
      if (!feedbackEl) return
      clearTimeout(feedbackHideTimer)
      feedbackEl.classList.remove('feniks-form-feedback--hidden', 'feniks-form-feedback--success')
      feedbackEl.classList.add('feniks-form-feedback--error')
      feedbackEl.setAttribute('role', 'alert')
      feedbackEl.innerHTML = ''
      const wrap = doc.createElement('div')
      wrap.className = 'feniks-form-feedback-inner'
      const icon = doc.createElement('div')
      icon.className = 'feniks-form-feedback-icon'
      icon.setAttribute('aria-hidden', 'true')
      icon.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/><path d="M12 8v5M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`
      const text = doc.createElement('div')
      text.className = 'feniks-form-feedback-text'
      const strong = doc.createElement('strong')
      strong.textContent = 'Не удалось отправить'
      const p = doc.createElement('p')
      p.textContent = message
      text.append(strong, p)
      wrap.append(icon, text)
      feedbackEl.appendChild(wrap)
      feedbackEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
    }

    form.addEventListener('submit', async (event) => {
      event.preventDefault()
      hideFeedback()
      if (!form.checkValidity()) {
        form.reportValidity()
        return
      }
      const fd = new FormData(form)
      const payload = {
        fullName: String(fd.get('fullName') ?? '').trim(),
        phone: String(fd.get('phone') ?? '').trim(),
        email: String(fd.get('email') ?? '').trim(),
        city: String(fd.get('city') ?? '').trim(),
      }
      const baseUrl = import.meta.env.VITE_FRANCHISE_LEAD_URL
      const url =
        typeof baseUrl === 'string' && baseUrl.length > 0
          ? baseUrl
          : '/api/v1/franchise/leads'
      const submitBtn = form.querySelector('.feniks-form-submit')
      const submitLabel = form.querySelector('.feniks-form-submit-text')
      const defaultLabel = 'Отправить заявку'
      try {
        if (submitBtn) {
          submitBtn.disabled = true
          submitBtn.classList.add('is-loading')
        }
        if (submitLabel) submitLabel.textContent = 'Отправляем…'
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        })
        if (res.ok) {
          form.reset()
          showFeedbackSuccess()
          return
        }
        let message = 'Попробуйте позже или напишите нам на почту.'
        try {
          const err = await res.json()
          if (err?.message) message = err.message
        } catch {
          /* ignore non-JSON body */
        }
        showFeedbackError(message)
      } catch {
        showFeedbackError('Нет связи с сервером. Проверьте подключение к интернету.')
      } finally {
        if (submitBtn) {
          submitBtn.disabled = false
          submitBtn.classList.remove('is-loading')
        }
        if (submitLabel) submitLabel.textContent = defaultLabel
      }
    })
  }

  const replaceContactsForm = () => {
    const formWrap = doc.querySelector('#contacts')
    if (!formWrap || formWrap.dataset.feniksFormReplaced === '1') return
    const inner = formWrap.querySelector('[class*="Form_Inner"]')
    if (!inner) return
    stripB24Recaptcha()
    feniksLeadMountSeq += 1
    const uid = `c${feniksLeadMountSeq}`
    inner.innerHTML = buildFeniksLeadRootHtml(uid)
    bindFeniksLeadForm(inner)
    formWrap.dataset.feniksFormReplaced = '1'
    stripB24Recaptcha()
  }

  const replaceBitrixB24LeadWrappers = () => {
    doc.querySelectorAll('.b24-form-wrapper').forEach((wrap) => {
      if (wrap.dataset.feniksB24LeadReplaced === '1') return
      if (wrap.querySelector('[class*="Form_Inner"]')) return
      if (wrap.closest('.feniks-lead-root')) return
      const title = wrap.querySelector('.b24-form-header-title')?.textContent?.trim() || ''
      const hasPhoneField = !!wrap.querySelector('[class*="b24-form-field-phone"]')
      const likelyLead = /заявк|отправить/i.test(title) || hasPhoneField
      if (!likelyLead) return
      wrap.dataset.feniksB24LeadReplaced = '1'
      wrap.classList.add('feniks-replaced-b24-lead')
      wrap.classList.remove('b24-form-shadow')
      feniksLeadMountSeq += 1
      const uid = `b${feniksLeadMountSeq}`
      stripB24Recaptcha()
      wrap.innerHTML = buildFeniksLeadRootHtml(uid)
      bindFeniksLeadForm(wrap)
      stripB24Recaptcha()
    })
  }

  let lastLeadFormOpen = 0
  const openLeadFormFromHero = () => {
    const now = typeof performance !== 'undefined' ? performance.now() : Date.now()
    if (now - lastLeadFormOpen < 400) return
    lastLeadFormOpen = now
    setFeniksAnchorIds()
    replaceContactsForm()
    replaceBitrixB24LeadWrappers()
    const target = doc.getElementById('contacts')
    if (target) {
      target.scrollIntoView({ behavior: 'smooth', block: 'start' })
      requestAnimationFrame(() => {
        doc.querySelector('#contacts input[name="fullName"]')?.focus({ preventScroll: true })
      })
    }
  }

  /**
   * React/Next перехватывает click на кнопке — слушатель на самой кнопке не срабатывает.
   * Вешаем capture на document + pointerdown и click, stopImmediatePropagation.
   */
  const wireTitleScreenLeadButtons = () => {
    if (doc.documentElement.dataset.feniksLeadHeroCapture === '1') return
    doc.documentElement.dataset.feniksLeadHeroCapture = '1'

    const shouldHandleButton = (btn) => {
      if (!(btn instanceof HTMLButtonElement)) return false
      if (btn.disabled) return false
      if (btn.classList.contains('custom-cta')) return true
      if (!btn.closest('[class*="TitleScreen_Buttons"]')) return false
      const span = btn.querySelector('span')
      const label = (span?.textContent || '').trim()
      const cls = btn.className || ''
      const isGreen =
        cls.includes('Green48') ||
        cls.includes('Button_Green') ||
        /Green\d{2}/i.test(cls)
      const labelSuggestsLead = /заявк|финмодел|финпоказател|финпоказ/i.test(label)
      return isGreen || labelSuggestsLead
    }

    const intercept = (e) => {
      const t = e.target
      if (!(t instanceof Element)) return
      const btn = t.closest('button')
      if (!(btn instanceof HTMLButtonElement) || !shouldHandleButton(btn)) return
      e.preventDefault()
      e.stopPropagation()
      e.stopImmediatePropagation()
      openLeadFormFromHero()
    }

    const win = doc.defaultView
    if (win) {
      win.addEventListener('pointerdown', intercept, true)
      win.addEventListener('click', intercept, true)
    } else {
      doc.addEventListener('pointerdown', intercept, true)
      doc.addEventListener('click', intercept, true)
    }
  }

  const footerMenuNeedsPatch = (menuEl) =>
    !menuEl.querySelector('.feniks-footer-nav-ul a[href="#contacts"]')

  const getFooterInfoForMenu = (menuEl) => {
    const footer = menuEl.closest('[class*="DesktopFooter_Footer"], [class*="TabletMobileFooter_Footer"]')
    if (!footer) return null
    if (menuEl.className.includes('DesktopFooter_Menu')) {
      return footer.querySelector('[class*="DesktopFooter_Info"]')
    }
    return footer.querySelector('[class*="TabletMobileFooter_Info"]')
  }

  const moveFooterPhoneIntoInfo = (info, phone) => {
    if (!info || !phone) return
    if (phone.parentElement === info) return
    const btn = info.querySelector('button')
    if (btn) btn.insertAdjacentElement('afterend', phone)
    else info.prepend(phone)
  }

  const restoreFooterPhoneToInfo = () => {
    doc
      .querySelectorAll(
        '[class*="DesktopFooter_Menu__"], [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"])',
      )
      .forEach((menuEl) => {
        const phone = menuEl.querySelector(':scope > a[class*="PhoneLink"]')
        if (!phone) return
        const info = getFooterInfoForMenu(menuEl)
        moveFooterPhoneIntoInfo(info, phone)
      })
  }

  const patchFooterBrandNav = () => {
    setFeniksAnchorIds()
    doc
      .querySelectorAll(
        '[class*="DesktopFooter_LogoLink"] img, [class*="TabletMobileFooter_LogoLink"] img, [class*="MobileFooter_LogoLink"] img',
      )
      .forEach((img) => {
        img.setAttribute('src', FENIKS_FOOTER_LOGO)
        img.removeAttribute('srcset')
        img.setAttribute('alt', 'Кофе Феникс')
      })
    doc
      .querySelectorAll('[class*="DesktopFooter_LogoLink"], [class*="TabletMobileFooter_LogoLink"]')
      .forEach((a) => {
        if (a.tagName === 'A') a.setAttribute('href', '#brand')
      })

    const buildNavUl = (ulClassName) => {
      const rows = [
        ['#brand', 'О бренде'],
        ['#package', 'Пакет'],
        ['#market', 'Рынок'],
        ['#contacts', 'Контакты'],
      ]
      const lis = rows
        .map(
          ([href, label]) =>
            `<li class="MenuItem_Item__MH443"><a class="MenuItem_Link__hbBhT" href="${href}">${label}</a></li>`,
        )
        .join('')
      return `<ul class="${ulClassName} feniks-footer-nav-ul">${lis}</ul>`
    }

    doc
      .querySelectorAll(
        '[class*="DesktopFooter_Menu__"], [class*="TabletMobileFooter_Menu__"]:not([class*="MenuTablet"])',
      )
      .forEach((menuEl) => {
      if (!footerMenuNeedsPatch(menuEl)) return
      const info = getFooterInfoForMenu(menuEl)
      const phone = menuEl.className.includes('DesktopFooter_Menu')
        ? doc.querySelector('a[class*="DesktopFooter_PhoneLink"]')
        : doc.querySelector('a[class*="TabletMobileFooter_PhoneLink"]')
      if (phone && info && menuEl.contains(phone)) {
        moveFooterPhoneIntoInfo(info, phone)
      }
      const sampleUl = menuEl.querySelector('ul')
      const ulClass =
        sampleUl?.className.replace(/\s*feniks-footer-nav-ul\b/g, '').trim() ||
        'MenuItem_Items__aF9sf'
      menuEl.innerHTML = buildNavUl(ulClass)
    })
  }

  const patchFooterPhone = () => {
    doc.querySelectorAll('a[data-feniks-instagram="1"]').forEach((el) => el.remove())
    doc.querySelectorAll('a[href*="74950153150"], a[href="tel:tel:+74950153150"]').forEach((a) => {
      a.setAttribute('href', FENIKS_FOOTER_PHONE_HREF)
    })
    doc.querySelectorAll('[class*="PhoneLink"]').forEach((a) => {
      if (a.tagName !== 'A') return
      a.setAttribute('href', FENIKS_FOOTER_PHONE_HREF)
      a.textContent = FENIKS_FOOTER_PHONE_TEXT
    })
  }

  const patchFooter = () => {
    patchFooterPhone()
    patchFooterBrandNav()
    restoreFooterPhoneToInfo()
    removeLegacySiteHeader()
    removeSuccessStoriesBlock()
    removePostFooterChrome()
  }

  const setupFooterContactObserver = () => {
    const win = doc.defaultView
    if (!win || !doc.body || doc.body.dataset.feniksFooterContactObs) return
    doc.body.dataset.feniksFooterContactObs = '1'
    if (!doc.body.dataset.feniksFooterResize) {
      doc.body.dataset.feniksFooterResize = '1'
      win.addEventListener(
        'resize',
        () => {
          requestAnimationFrame(() => patchFooter())
        },
        { passive: true },
      )
    }
    let raf = 0
    let debounceTimer = 0
    const obs = new MutationObserver(() => {
      win.clearTimeout(debounceTimer)
      debounceTimer = win.setTimeout(() => {
        if (raf) win.cancelAnimationFrame(raf)
        raf = win.requestAnimationFrame(() => {
          raf = 0
          patchFooter()
        })
      }, 120)
    })
    obs.observe(doc.body, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ['href', 'src', 'srcset'],
    })
  }

  patchFooter()
  setTimeout(patchFooter, 300)
  setTimeout(patchFooter, 1200)
  setupFooterContactObserver()
  const iframeWin = doc.defaultView
  const deferFeniksDomPatches = () => {
    replaceContactsForm()
    replaceBitrixB24LeadWrappers()
    stripB24Recaptcha()
  }
  if (iframeWin) {
    iframeWin.setTimeout(deferFeniksDomPatches, 150)
  } else {
    deferFeniksDomPatches()
  }
  setTimeout(replaceContactsForm, 500)
  setTimeout(replaceContactsForm, 1500)
  setTimeout(replaceBitrixB24LeadWrappers, 350)
  setTimeout(replaceBitrixB24LeadWrappers, 900)
  setTimeout(replaceBitrixB24LeadWrappers, 1800)
  setTimeout(stripB24Recaptcha, 400)
  setTimeout(stripB24Recaptcha, 1200)
  setTimeout(stripB24Recaptcha, 2500)
  wireTitleScreenLeadButtons()
  if (iframeEl?.tagName === 'IFRAME') {
    iframeEl.__feniksScrollToLeadForm = openLeadFormFromHero
  }
}
</script>

<template>
  <main class="franchise-page">
    <header class="custom-header">
      <div class="custom-header-inner">
        <a class="custom-logo" href="/">
          <img src="/legacy/icon/logocoff.svg" alt="Feniks logo" />
        </a>
        <nav class="custom-nav">
          <a href="#brand">О бренде</a>
          <a href="#package">Пакет</a>
          <a href="#market">Рынок</a>
          <a href="#contacts">Контакты</a>
        </nav>
        <button class="custom-cta" type="button" @click="openLeadModal">Оставить заявку</button>
      </div>
    </header>

    <Teleport to="body">
      <div v-if="leadModalOpen" class="feniks-lead-modal-root" role="presentation">
        <button
          type="button"
          class="feniks-lead-modal-backdrop"
          aria-label="Закрыть окно"
          tabindex="-1"
          @click="closeLeadModal"
        />
        <div
          class="feniks-lead-modal-dialog"
          role="dialog"
          aria-modal="true"
          aria-labelledby="feniks-lead-modal-title"
          @click.stop
        >
          <button type="button" class="feniks-lead-modal-close" aria-label="Закрыть" @click="closeLeadModal">
            ×
          </button>
          <div class="feniks-lead-modal-header">
            <h2 id="feniks-lead-modal-title" class="feniks-lead-modal-title">Заявка на франшизу</h2>
            <p class="feniks-lead-modal-lead">
              Оставьте контакты — перезвоним и ответим на вопросы по пакету и запуску.
            </p>
          </div>
          <div
            v-if="leadModalFeedback === 'success'"
            class="feniks-lead-modal-banner feniks-lead-modal-banner--success"
            role="status"
          >
            <strong>Заявка отправлена</strong>
            <p>Спасибо! Мы свяжемся с вами в ближайшее время.</p>
            <button type="button" class="feniks-lead-modal-banner-btn" @click="closeLeadModal">Закрыть</button>
          </div>
          <div
            v-else-if="leadModalFeedback === 'error'"
            class="feniks-lead-modal-banner feniks-lead-modal-banner--error"
            role="alert"
          >
            <strong>Не удалось отправить</strong>
            <p>{{ leadModalErrorMsg }}</p>
          </div>
          <form
            v-show="leadModalFeedback !== 'success'"
            ref="leadModalFormRef"
            class="feniks-lead-modal-form"
            novalidate
            @submit.prevent="submitLeadModal"
          >
            <div class="feniks-lead-modal-grid">
              <div class="feniks-lead-modal-field feniks-lead-modal-field--full">
                <label for="feniks-modal-fullname">ФИО</label>
                <span class="feniks-lead-modal-hint">Введите ФИО</span>
                <input
                  id="feniks-modal-fullname"
                  name="fullName"
                  type="text"
                  required
                  placeholder="Иванов Иван Иванович"
                  autocomplete="name"
                />
              </div>
              <div class="feniks-lead-modal-field">
                <label for="feniks-modal-phone">Телефон</label>
                <input
                  id="feniks-modal-phone"
                  name="phone"
                  type="tel"
                  required
                  placeholder="+7 (___) ___-__-__"
                  autocomplete="tel"
                />
              </div>
              <div class="feniks-lead-modal-field">
                <label for="feniks-modal-email">E-mail</label>
                <input
                  id="feniks-modal-email"
                  name="email"
                  type="email"
                  required
                  placeholder="example@mail.ru"
                  autocomplete="email"
                />
              </div>
              <div class="feniks-lead-modal-field feniks-lead-modal-field--full">
                <label for="feniks-modal-city">Город</label>
                <input
                  id="feniks-modal-city"
                  name="city"
                  type="text"
                  required
                  placeholder="Введите город"
                  autocomplete="address-level2"
                />
              </div>
            </div>
            <p class="feniks-lead-modal-note">
              Нажимая «Отправить», вы соглашаетесь на обработку персональных данных.
            </p>
            <button class="feniks-lead-modal-submit" type="submit" :disabled="leadModalSubmitting">
              <span>{{ leadModalSubmitting ? 'Отправляем…' : 'Отправить заявку' }}</span>
            </button>
          </form>
        </div>
      </div>
    </Teleport>

    <div class="legacy-shell">
      <iframe
        ref="legacyFrameRef"
        class="legacy-frame"
        :class="{ 'legacy-frame-ready': frameReady }"
        :src="frameSrc"
        title="Franchise page"
        @load="applyHeroBackground"
      />
    </div>
  </main>
</template>
