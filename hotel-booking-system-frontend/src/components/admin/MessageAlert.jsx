import { Alert } from "../ui";

function MessageAlert({ type, message }) {
  if (!message) return null;

  return <Alert type={type === "error" ? "error" : "success"}>{message}</Alert>;
}

export default MessageAlert;
