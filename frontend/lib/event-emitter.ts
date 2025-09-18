// Простой event emitter для отправки событий из API routes в UI
class EventEmitter {
  private static instance: EventEmitter
  private listeners: Map<string, Function[]> = new Map()

  private constructor() {}

  public static getInstance(): EventEmitter {
    if (!EventEmitter.instance) {
      EventEmitter.instance = new EventEmitter()
    }
    return EventEmitter.instance
  }

  public on(event: string, callback: Function) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event)!.push(callback)
  }

  public off(event: string, callback: Function) {
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event)!
      const index = callbacks.indexOf(callback)
      if (index > -1) {
        callbacks.splice(index, 1)
      }
    }
  }

  public emit(event: string, data: any) {
    console.log('📡 EventEmitter: Emitting event:', event, data)
    if (this.listeners.has(event)) {
      this.listeners.get(event)!.forEach(callback => {
        try {
          callback(data)
        } catch (error) {
          console.error('EventEmitter: Error in callback:', error)
        }
      })
    }
  }
}

export const eventEmitter = EventEmitter.getInstance()
