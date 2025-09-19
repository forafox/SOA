import { NextResponse } from 'next/server'
import { addCallback, getRecentCallbacks } from '@/lib/callback-storage'

export async function GET() {
  const callbacks = getRecentCallbacks()
  console.log('📋 Recent Callbacks: Returning callbacks:', callbacks.length)
  return NextResponse.json({ callbacks })
}

export async function POST(request: Request) {
  try {
    const body = await request.json()
    const created = addCallback(body)
    console.log('📋 Recent Callbacks: Added via storage:', created.id)
    return NextResponse.json({ success: true, callback: created })
  } catch (error) {
    console.error('Error adding recent callback:', error)
    return NextResponse.json(
      { success: false, error: 'Failed to add callback' },
      { status: 500 }
    )
  }
}
