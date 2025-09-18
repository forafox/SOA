import { NextResponse } from 'next/server'

// Простая заглушка - больше не сохраняем коллбэки
export async function GET() {
  console.log('📋 Callback List: No callbacks stored (using event-based system)')
  return NextResponse.json({ callbacks: [] })
}

export async function POST() {
  console.log('📋 Callback List: POST not supported (using event-based system)')
  return NextResponse.json({ 
    success: false, 
    error: 'Callbacks are now handled via events, not stored' 
  }, { status: 400 })
}
