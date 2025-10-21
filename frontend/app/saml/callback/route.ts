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
      console.log('SAML Response length:', samlResponse?.length || 0);
    } catch (err) {
      console.warn('formData() failed, using manual parser', err);
      const rawBody = await request.text();
      console.log('Raw body length:', rawBody.length);
      const params = parseFormBody(rawBody);
      samlResponse = params['SAMLResponse'];
      relayState = params['RelayState'] || null;
    }

    if (!samlResponse) {
      console.error('Missing SAMLResponse');
      return new NextResponse('Missing SAMLResponse', { status: 400 });
    }

    // Используем хардкод для localhost, так как переменные окружения не работают
    const finalOrigin = 'http://localhost:3000';
    console.log('Using hardcoded origin:', finalOrigin);
    
    try {
      const target = new URL('/', finalOrigin);
      target.searchParams.set('SAMLResponse', samlResponse);
      if (relayState) target.searchParams.set('RelayState', relayState);

      console.log('Redirect URL:', target.toString());
      return NextResponse.redirect(target.toString(), { status: 302 });
    } catch (urlError) {
      console.error('URL creation error:', urlError);
      // Fallback to simple redirect
      let fallbackUrl = `${finalOrigin}/?SAMLResponse=${encodeURIComponent(samlResponse)}`;
      if (relayState) {
        fallbackUrl += `&RelayState=${encodeURIComponent(relayState)}`;
      }
      console.log('Fallback URL:', fallbackUrl);
      return NextResponse.redirect(fallbackUrl, { status: 302 });
    }
  } catch (err) {
    console.error('SAML callback error:', err);
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}

export async function GET() {
  return new NextResponse('SAML ACS endpoint. Use HTTP POST.', { status: 405 });
}