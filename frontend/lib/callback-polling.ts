// Простой polling механизм для проверки коллбэков
interface CallbackData {
  id: string
  type: string
  data: unknown
  status: string
  timestamp: string
}

class CallbackPoller {
  private static instance: CallbackPoller
  private callbacks: CallbackData[] = []
  private listeners: ((callbacks: CallbackData[]) => void)[] = []

  private constructor() {}

  public static getInstance(): CallbackPoller {
    if (!CallbackPoller.instance) {
      CallbackPoller.instance = new CallbackPoller()
    }
    return CallbackPoller.instance
  }

  public addCallback(callback: CallbackData) {
    console.log('📡 CallbackPoller: Adding callback:', callback)
    this.callbacks.push(callback)
    this.notifyListeners()
  }

  public getCallbacks() {
    return [...this.callbacks]
  }

  public onUpdate(callback: (callbacks: CallbackData[]) => void) {
    this.listeners.push(callback)
  }

  public offUpdate(callback: (callbacks: CallbackData[]) => void) {
    const index = this.listeners.indexOf(callback)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }

  private notifyListeners() {
    this.listeners.forEach(listener => {
      try {
        listener(this.callbacks)
      } catch (error) {
        console.error('CallbackPoller: Error in listener:', error)
      }
    })
  }
}

export const callbackPoller = CallbackPoller.getInstance()
