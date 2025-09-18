import { NextResponse } from 'next/server'
import { getRecentCallbacks } from '@/lib/callback-storage'

export async function GET() {
  const callbacks = getRecentCallbacks()
  console.log('📋 Recent Callbacks: Returning callbacks:', callbacks.length)
  return NextResponse.json({ callbacks })
}

export async function POST(request: Request) {
  try {
    const body = await request.json()
    const callback = {
      ...body,
      id: Math.random().toString(36).substr(2, 9),
      timestamp: new Date().toISOString()
    }
    
    console.log('📋 Recent Callbacks: Adding callback:', callback)
    
    // Добавляем коллбэк в начало списка
    const callbacks = getRecentCallbacks()
    callbacks.unshift(callback)
    callbacks.splice(10) // Ограничиваем до 10 коллбэков
    
    return NextResponse.json({ success: true, callback })
  } catch (error) {
    console.error('Error adding recent callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to add callback' },
      { status: 500 }
    )
  }
}
