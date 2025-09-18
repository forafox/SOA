import { NextRequest, NextResponse } from 'next/server'
import { addCallback } from '@/lib/callback-storage'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    
    console.log('🎬 API Route: Callback onAwarded received:', body)
    
    // Создаем и сохраняем коллбэк
    const callback = addCallback({
      type: 'onAwarded',
      data: body,
      status: 'success'
    })
    
    console.log('🎬 API Route: Callback saved to recent storage:', callback.id)
    
    // Возвращаем данные коллбэка, чтобы UI мог их обработать
    return NextResponse.json({ 
      success: true, 
      message: 'Callback onAwarded processed successfully',
      callback
    })
  } catch (error) {
    console.error('Error processing onAwarded callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to process callback' },
      { status: 500 }
    )
  }
}
