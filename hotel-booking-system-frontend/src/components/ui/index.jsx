import { useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { AlertCircle, ArrowLeft, CheckCircle2, Info, Loader2, Trash2, X } from "lucide-react";
import PropTypes from "prop-types";

const cx = (...classes) => classes.filter(Boolean).join(" ");

export function Button({
  as: Component = "button",
  to,
  href,
  variant = "primary",
  size = "md",
  icon,
  iconOnly = false,
  className = "",
  children,
  type,
  ...props
}) {
  const classes = cx(
    "ui-button",
    `ui-button--${variant}`,
    `ui-button--${size}`,
    iconOnly && "ui-button--icon-only",
    className
  );

  const content = (
    <>
      {icon && <span className="ui-button__icon">{icon}</span>}
      {!iconOnly && children}
    </>
  );

  if (to) {
    return (
      <Link to={to} className={classes} {...props}>
        {content}
      </Link>
    );
  }

  if (href) {
    return (
      <a href={href} className={classes} {...props}>
        {content}
      </a>
    );
  }

  return (
    <Component className={classes} type={type ?? "button"} {...props}>
      {content}
    </Component>
  );
}

Button.propTypes = {
  as: PropTypes.elementType,
  to: PropTypes.string,
  href: PropTypes.string,
  variant: PropTypes.oneOf([
    "primary",
    "secondary",
    "ghost",
    "danger",
    "success",
    "dark",
    "back",
  ]),
  size: PropTypes.oneOf(["sm", "md", "lg"]),
  icon: PropTypes.node,
  iconOnly: PropTypes.bool,
  className: PropTypes.string,
  children: PropTypes.node,
  type: PropTypes.string,
};

export function DangerButton({ icon = <Trash2 size={14} />, children = "Remove", ...props }) {
  return (
    <Button variant="danger" icon={icon} {...props}>
      {children}
    </Button>
  );
}

DangerButton.propTypes = {
  icon: PropTypes.node,
  children: PropTypes.node,
};

export function BackButton({ to, onClick, label = "Back", className = "" }) {
  return (
    <Button
      to={to}
      onClick={onClick}
      variant="back"
      size="sm"
      icon={<ArrowLeft size={16} />}
      className={className}
    >
      {label}
    </Button>
  );
}

BackButton.propTypes = {
  to: PropTypes.string,
  onClick: PropTypes.func,
  label: PropTypes.string,
  className: PropTypes.string,
};

export function PageToolbar({ children, className = "" }) {
  if (!children) return null;
  return <div className={cx("ui-page-toolbar", className)}>{children}</div>;
}

PageToolbar.propTypes = {
  children: PropTypes.node,
  className: PropTypes.string,
};

export function RoleChoiceCard({ icon, title, description, to }) {
  return (
    <Link to={to} className="role-choice-card">
      <span className="role-choice-card__icon">{icon}</span>
      <span>
        <strong>{title}</strong>
        <small>{description}</small>
      </span>
    </Link>
  );
}

RoleChoiceCard.propTypes = {
  icon: PropTypes.node.isRequired,
  title: PropTypes.string.isRequired,
  description: PropTypes.string.isRequired,
  to: PropTypes.string.isRequired,
};

export function PageShell({
  eyebrow,
  title,
  description,
  actions,
  toolbar,
  children,
  className = "",
  narrow = false,
}) {
  return (
    <main className={cx("ui-page", className)}>
      <div className={cx("ui-page__container", narrow && "ui-page__container--narrow")}>
        {toolbar && <PageToolbar>{toolbar}</PageToolbar>}
        {(title || description || actions) && (
          <header className="ui-page__header">
            <div>
              {eyebrow && <p className="ui-eyebrow">{eyebrow}</p>}
              {title && <h1>{title}</h1>}
              {description && <p>{description}</p>}
            </div>
            {actions && <div className="ui-page__actions">{actions}</div>}
          </header>
        )}
        {children}
      </div>
    </main>
  );
}

PageShell.propTypes = {
  eyebrow: PropTypes.string,
  title: PropTypes.string,
  description: PropTypes.string,
  actions: PropTypes.node,
  toolbar: PropTypes.node,
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
  narrow: PropTypes.bool,
};

export function Card({ as: Component = "section", className = "", children, ...props }) {
  return (
    <Component className={cx("ui-card", className)} {...props}>
      {children}
    </Component>
  );
}

Card.propTypes = {
  as: PropTypes.elementType,
  className: PropTypes.string,
  children: PropTypes.node.isRequired,
};

export function SectionHeader({ eyebrow, title, description, actions, className = "" }) {
  return (
    <div className={cx("ui-section-header", className)}>
      <div>
        {eyebrow && <p className="ui-eyebrow">{eyebrow}</p>}
        <h2>{title}</h2>
        {description && <p>{description}</p>}
      </div>
      {actions && <div className="ui-section-header__actions">{actions}</div>}
    </div>
  );
}

SectionHeader.propTypes = {
  eyebrow: PropTypes.string,
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  actions: PropTypes.node,
  className: PropTypes.string,
};

export function Badge({ children, tone = "neutral", className = "" }) {
  return <span className={cx("ui-badge", `ui-badge--${tone}`, className)}>{children}</span>;
}

Badge.propTypes = {
  children: PropTypes.node.isRequired,
  tone: PropTypes.oneOf(["neutral", "info", "success", "warning", "danger", "dark"]),
  className: PropTypes.string,
};

const alertIcons = {
  info: <Info size={18} />,
  success: <CheckCircle2 size={18} />,
  error: <AlertCircle size={18} />,
  warning: <AlertCircle size={18} />,
};

export function Alert({ type = "info", title, children, className = "" }) {
  if (!children && !title) return null;

  return (
    <div className={cx("ui-alert", `ui-alert--${type}`, className)} role={type === "error" ? "alert" : "status"}>
      <span className="ui-alert__icon">{alertIcons[type] ?? alertIcons.info}</span>
      <div>
        {title && <strong>{title}</strong>}
        {children && <p>{children}</p>}
      </div>
    </div>
  );
}

Alert.propTypes = {
  type: PropTypes.oneOf(["info", "success", "error", "warning"]),
  title: PropTypes.string,
  children: PropTypes.node,
  className: PropTypes.string,
};

export function EmptyState({ icon, title, description, actions, className = "" }) {
  return (
    <div className={cx("ui-empty-state", className)}>
      {icon && <div className="ui-empty-state__icon">{icon}</div>}
      <h3>{title}</h3>
      {description && <p>{description}</p>}
      {actions && <div className="ui-empty-state__actions">{actions}</div>}
    </div>
  );
}

EmptyState.propTypes = {
  icon: PropTypes.node,
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  actions: PropTypes.node,
  className: PropTypes.string,
};

export function LoadingState({ label = "Loading..." }) {
  return (
    <div className="ui-loading-state" role="status" aria-live="polite">
      <Loader2 size={20} className="ui-spin" />
      <span>{label}</span>
    </div>
  );
}

LoadingState.propTypes = {
  label: PropTypes.string,
};

export function DataTable({ children, className = "", "aria-label": ariaLabel }) {
  return (
    <div className={cx("ui-table-wrap", className)}>
      <table className="ui-data-table" aria-label={ariaLabel}>
        {children}
      </table>
    </div>
  );
}

DataTable.propTypes = {
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
  "aria-label": PropTypes.string,
};

export function Modal({
  open,
  onClose,
  title,
  children,
  footer,
  className = "",
  closeLabel = "Close dialog",
}) {
  const dialogRef = useRef(null);

  useEffect(() => {
    if (!open) return undefined;

    const handleKeyDown = (event) => {
      if (event.key === "Escape") onClose?.();
    };

    document.addEventListener("keydown", handleKeyDown);
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    window.setTimeout(() => dialogRef.current?.focus(), 0);

    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = previousOverflow;
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="ui-modal-backdrop" onMouseDown={onClose}>
      <div
        className={cx("ui-modal", className)}
        role="dialog"
        aria-modal="true"
        aria-labelledby="ui-modal-title"
        tabIndex={-1}
        ref={dialogRef}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="ui-modal__header">
          <h2 id="ui-modal-title">{title}</h2>
          <button type="button" className="ui-modal__close" onClick={onClose} aria-label={closeLabel}>
            <X size={18} />
          </button>
        </div>
        <div className="ui-modal__body">{children}</div>
        {footer && <div className="ui-modal__footer">{footer}</div>}
      </div>
    </div>
  );
}

Modal.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func,
  title: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
  footer: PropTypes.node,
  className: PropTypes.string,
  closeLabel: PropTypes.string,
};

export function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  tone = "danger",
  onConfirm,
  onCancel,
}) {
  return (
    <Modal
      open={open}
      onClose={onCancel}
      title={title}
      footer={
        <>
          <Button variant="secondary" onClick={onCancel}>
            {cancelLabel}
          </Button>
          <Button variant={tone} onClick={onConfirm}>
            {confirmLabel}
          </Button>
        </>
      }
    >
      <p className="ui-confirm-message">{message}</p>
    </Modal>
  );
}

ConfirmDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  title: PropTypes.string.isRequired,
  message: PropTypes.string.isRequired,
  confirmLabel: PropTypes.string,
  cancelLabel: PropTypes.string,
  tone: PropTypes.oneOf(["primary", "danger", "success", "dark"]),
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};
