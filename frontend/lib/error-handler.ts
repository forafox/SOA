import { ApiError } from "./api-client"

export function getErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    return error.message
  }

  if (error instanceof Error) {
    return error.message
  }

  return "Произошла неизвестная ошибка"
}

export function getErrorDetails(error: unknown): {
  message: string
  status?: number
  isValidationError: boolean
  isNetworkError: boolean
} {
  if (error instanceof ApiError) {
    return {
      message: error.message,
      status: error.status,
      isValidationError: error.status === 422,
      isNetworkError: false,
    }
  }

  if (error instanceof TypeError && error.message.includes("fetch")) {
    return {
      message: "Ошибка сети. Проверьте подключение к серверу.",
      isValidationError: false,
      isNetworkError: true,
    }
  }

  return {
    message: getErrorMessage(error),
    isValidationError: false,
    isNetworkError: false,
  }
}
