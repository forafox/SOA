import { NextResponse } from 'next/server';

export const runtime = 'nodejs';

// Ручной парсер urlencoded тела
function parseFormBody(body: string): Record<string, string> {
  const result: Record<string, string> = {};
  body.split('&').forEach(pair => {
    const [key, val] = pair.split('=');
    if (key && val) {
      result[decodeURIComponent(key)] = decodeURIComponent(val.replace(/\+/g, ' '));
    }
  });
  return result;
}

export async function POST(request: Request) {
  let samlResponse: string | null = null;
  let relayState: string | null = null;

  try {
    console.log('SAML callback POST received');

    try {
      const formData = await request.formData();
      samlResponse = formData.get('SAMLResponse') as string;
      relayState = formData.get('RelayState') as string | null;
    } catch (err) {
      console.warn('formData() failed, using manual parser', err);
      const rawBody = await request.text();
      const params = parseFormBody(rawBody);
      samlResponse = params['SAMLResponse'];
      relayState = params['RelayState'] || null;
    }

    if (!samlResponse) return new NextResponse('Missing SAMLResponse', { status: 400 });

    // Только env, больше не используем request.url
    const envOrigin = process.env.NEXT_PUBLIC_APP_ORIGIN?.trim() || 'http://localhost:3000';
    const target = new URL('/', envOrigin);

    target.searchParams.set('SAMLResponse', samlResponse);
    if (relayState) target.searchParams.set('RelayState', relayState);

    console.log('Redirect URL:', target.toString());
    return NextResponse.redirect(target.toString(), { status: 302 });
  } catch (err) {
    console.error('SAML callback error:', err);
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}

export async function GET() {
  return new NextResponse('SAML ACS endpoint. Use HTTP POST.', { status: 405 });
}