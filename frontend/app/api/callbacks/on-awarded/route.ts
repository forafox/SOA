import { NextRequest, NextResponse } from 'next/server'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    
    console.log('Callback onAwarded received:', body)
    
    // Возвращаем данные коллбэка, чтобы UI мог их обработать
    return NextResponse.json({ 
      success: true, 
      message: 'Callback onAwarded processed successfully',
      callback: {
        type: 'onAwarded',
        data: body,
        status: 'success',
        timestamp: new Date().toISOString()
      }
    })
  } catch (error) {
    console.error('Error processing onAwarded callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to process callback' },
      { status: 500 }
    )
  }
}
