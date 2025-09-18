import { NextRequest, NextResponse } from 'next/server'

// Простое хранилище коллбэков в памяти
let recentCallbacks: any[] = []

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    
    console.log('🎬 API Route: Callback notifyOscarsTeam received:', body)
    
    // Создаем коллбэк
    const callback = {
      id: Math.random().toString(36).substr(2, 9),
      type: 'notifyOscarsTeam',
      data: body,
      status: 'success',
      timestamp: new Date().toISOString()
    }
    
    // Добавляем в хранилище
    recentCallbacks.unshift(callback)
    recentCallbacks = recentCallbacks.slice(0, 10) // Ограничиваем до 10 последних
    
    console.log('🎬 API Route: Callback saved to recent storage:', callback.id)
    console.log('🎬 API Route: Total callbacks in storage:', recentCallbacks.length)
    
    return NextResponse.json({ 
      success: true, 
      message: 'Callback notifyOscarsTeam processed successfully',
      callback
    })
  } catch (error) {
    console.error('Error processing notifyOscarsTeam callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to process callback' },
      { status: 500 }
    )
  }
}

// Функция для получения коллбэков
export function getRecentCallbacks() {
  return recentCallbacks
}
