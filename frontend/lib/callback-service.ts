import { eventEmitter } from "./event-emitter"
import { callbackUrls } from "./config"

export interface CallbackData {
  movieId?: number
  newOscarsCount?: number
  addedOscars?: number
  category?: string
  date?: string
  updatedMovies?: unknown[]
}

export class CallbackService {
  private static instance: CallbackService
  private toast: ((options: { title: string; description: string; duration?: number; className?: string }) => void) | null = null

  private constructor() {}

  public static getInstance(): CallbackService {
    if (!CallbackService.instance) {
      CallbackService.instance = new CallbackService()
    }
    return CallbackService.instance
  }

  public setToast(toast: (options: { title: string; description: string; duration?: number; className?: string }) => void) {
    this.toast = toast
  }

  public handleOnAwarded(data: CallbackData) {
    console.log('Processing onAwarded callback:', data)
    
    // Отправляем событие в UI
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('callback-received', {
        detail: { type: 'onAwarded', data, status: 'success' }
      }))
    }
    
    if (this.toast) {
      this.toast({
        title: "🎬 Фильмы награждены!",
        description: `Получено уведомление о награждении фильмов по длине. Обработано ${data.updatedMovies?.length || 0} фильмов.`,
        duration: 8000,
        className: "border-green-500 bg-green-50 text-green-900",
      })
    }
  }

  public handleNotifyAdmins(data: CallbackData) {
    console.log('Processing notifyAdmins callback:', data)
    
    // Отправляем событие в UI
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('callback-received', {
        detail: { type: 'notifyAdmins', data, status: 'success' }
      }))
    }
    
    if (this.toast) {
      this.toast({
        title: "👥 Уведомление администраторам",
        description: `Получено уведомление для администраторов о награждении ${data.updatedMovies?.length || 0} фильмов с малым количеством Оскаров.`,
        duration: 8000,
        className: "border-blue-500 bg-blue-50 text-blue-900",
      })
    }
  }

  public handleNotifyOscarsTeam(data: CallbackData) {
    console.log('🏆 CallbackService: Processing notifyOscarsTeam callback:', data)
    
    // Отправляем событие через event emitter
    console.log('🏆 CallbackService: Emitting event via event emitter')
    eventEmitter.emit('callback-received', { 
      type: 'notifyOscarsTeam', 
      data, 
      status: 'success' 
    })
    
    // Также отправляем через window events для совместимости
    if (typeof window !== 'undefined') {
      console.log('🏆 CallbackService: Sending event to UI via window')
      window.dispatchEvent(new CustomEvent('callback-received', {
        detail: { type: 'notifyOscarsTeam', data, status: 'success' }
      }))
      console.log('🏆 CallbackService: Event sent to UI via window')
    } else {
      console.log('🏆 CallbackService: window is undefined, cannot send event via window')
    }
    
    if (this.toast) {
      console.log('🏆 CallbackService: Showing toast notification')
      this.toast({
        title: "🏆 Команда Оскаров уведомлена",
        description: `Получено уведомление для команды Оскаров о добавлении ${data.addedOscars || 0} Оскаров к фильму ID ${data.movieId}.`,
        duration: 8000,
        className: "border-purple-500 bg-purple-50 text-purple-900",
      })
    } else {
      console.log('🏆 CallbackService: No toast available')
    }
  }

  public getCallbackUrls() {
    return callbackUrls
  }
}

export const callbackService = CallbackService.getInstance()
