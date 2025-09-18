// Простой polling механизм для проверки коллбэков
class CallbackPoller {
  private static instance: CallbackPoller
  private callbacks: any[] = []
  private listeners: Function[] = []

  private constructor() {}

  public static getInstance(): CallbackPoller {
    if (!CallbackPoller.instance) {
      CallbackPoller.instance = new CallbackPoller()
    }
    return CallbackPoller.instance
  }

  public addCallback(callback: any) {
    console.log('📡 CallbackPoller: Adding callback:', callback)
    this.callbacks.push(callback)
    this.notifyListeners()
  }

  public getCallbacks() {
    return [...this.callbacks]
  }

  public onUpdate(callback: Function) {
    this.listeners.push(callback)
  }

  public offUpdate(callback: Function) {
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
