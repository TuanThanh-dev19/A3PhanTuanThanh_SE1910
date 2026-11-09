// Author: Phan Tuan Thanh - CE190595
import { useEffect, useId, useRef } from 'react';

export default function Modal({
  title,
  onClose,
  busy = false,
  compact = false,
  wide = false,
  showClose = false,
  children,
}) {
  const dialogRef = useRef(null);
  const titleId = useId();

  useEffect(() => {
    const dialog = dialogRef.current;
    const previouslyFocused = document.activeElement;
    const previousOverflow = document.body.style.overflow;
    dialog.showModal();
    dialog.querySelector('[data-modal-focus]')?.focus();
    document.body.style.overflow = 'hidden';
    return () => {
      dialog.close();
      document.body.style.overflow = previousOverflow;
      if (previouslyFocused?.isConnected) previouslyFocused.focus();
    };
  }, []);

  function keepFocusInside(event) {
    if (event.key !== 'Tab') return;
    const controls = [
      ...event.currentTarget.querySelectorAll(
        'button, input, select, textarea, a[href], [tabindex]',
      ),
    ].filter(
      (element) =>
        !element.matches(':disabled') && element.tabIndex >= 0 && element.getClientRects().length,
    );
    const first = controls[0];
    const last = controls[controls.length - 1];
    if (!first) {
      event.preventDefault();
    } else if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  return (
    <dialog
      ref={dialogRef}
      className={`modal${compact ? ' modal-compact' : ''}${wide ? ' modal-wide' : ''}`}
      aria-labelledby={titleId}
      aria-modal="true"
      onKeyDown={keepFocusInside}
      onCancel={(event) => {
        event.preventDefault();
        if (!busy) onClose();
      }}
    >
      <div className="modal-heading">
        <h2 id={titleId}>{title}</h2>
        {showClose && (
          <button
            type="button"
            className="secondary"
            aria-label={`Close ${title.toLowerCase()}`}
            disabled={busy}
            onClick={onClose}
          >
            Close
          </button>
        )}
      </div>
      <div className="modal-content">{children}</div>
    </dialog>
  );
}
