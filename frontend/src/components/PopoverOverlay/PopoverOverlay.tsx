import cn from "classnames";
import React, { ReactElement, ReactNode } from "react";

import {
  Placement,
  Popover2,
  Popover2InteractionKind,
} from "@blueprintjs/popover2";
import "@blueprintjs/popover2/lib/css/blueprint-popover2.css";

import styles from "./PopoverOverlay.module.scss";

type popoverBorderColors = keyof typeof PopoverVariants;

export enum PopoverVariants {
  primary = "primary",
  gray = "gray",
  default = "default",
}

export interface IProps {
  children: ReactNode;
  content: ReactElement;
  placement?: Placement;
  className?: string;
  popoverClassName?: string;
  portalClassName?: string;
  popoverBorderColor?: popoverBorderColors;
  isDisabled?: boolean;
  isOpen?: boolean;
  openType?: Popover2InteractionKind;
  onInteraction?: (
    nextOpenState: boolean,
    e?: React.SyntheticEvent<HTMLElement>
  ) => void;
  onOpen?: () => void;
  onClose?: () => void;
}

const PopoverOverlay = React.forwardRef<HTMLElement, IProps>(
  (
    {
      isOpen,
      onClose,
      onInteraction,
      placement = "auto",
      children,
      content,
      className,
      popoverBorderColor = "primary",
      popoverClassName,
      isDisabled = false,
      openType = "click",
      onOpen,
      portalClassName,
    },
    ref
  ) => {
    return (
      <Popover2
        onClose={onClose}
        popoverRef={ref || undefined}
        isOpen={isOpen}
        onInteraction={onInteraction}
        content={content}
        placement={placement}
        disabled={isDisabled}
        interactionKind={openType}
        rootBoundary={"viewport"}
        className={className}
        popoverClassName={cn(
          popoverBorderColor && styles[popoverBorderColor],
          popoverClassName
        )}
        portalClassName={cn(styles.portal, portalClassName)}
        onOpened={onOpen}
        hoverOpenDelay={0}
        hoverCloseDelay={0}
        transitionDuration={0}
        enforceFocus={false}
      >
        {children}
      </Popover2>
    );
  }
);

export default PopoverOverlay;
