import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { AlertCircle, Wifi, AlertTriangle } from "lucide-react"
import { getErrorDetails } from "@/lib/error-handler"

interface ErrorDisplayProps {
  error: unknown
  title?: string
}

export function ErrorDisplay({ error, title = "Ошибка" }: ErrorDisplayProps) {
  const { message, status, isValidationError, isNetworkError } = getErrorDetails(error)

  const getIcon = () => {
    if (isNetworkError) return <Wifi className="h-4 w-4" />
    if (isValidationError) return <AlertTriangle className="h-4 w-4" />
    return <AlertCircle className="h-4 w-4" />
  }

  const getVariant = () => {
    if (isValidationError) return "default"
    return "destructive"
  }

  return (
    <Alert variant={getVariant()}>
      {getIcon()}
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>
        {message}
        {status && <span className="block text-sm text-muted-foreground mt-1">Код ошибки: {status}</span>}
      </AlertDescription>
    </Alert>
  )
}
