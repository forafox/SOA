import { NextRequest, NextResponse } from 'next/server'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const { type, data } = body
    
    console.log('Test callback received:', { type, data })
    
    // Возвращаем данные коллбэка сразу (для тестирования)
    return NextResponse.json({ 
      success: true, 
      message: `Test callback ${type} processed successfully`,
      callback: {
        type,
        data,
        status: 'success',
        timestamp: new Date().toISOString()
      }
    })
  } catch (error) {
    console.error('Error processing test callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to process test callback' },
      { status: 500 }
    )
  }
}
