import React, { useState } from 'react'
import { useLocaleText } from '@mui/x-date-pickers/internals';
import { unstable_useId as useId } from '@mui/utils';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { MenuItem, DialogActions, Button, Menu } from '@mui/material';
function CustomActionBar(props) {
  const { onAccept, onClear, onCancel, onSetToday, actions, className } = props;
  const localeText = useLocaleText();
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);
  const id = useId();

  if (actions == null || actions.length === 0) {
    return null;
  }

  const menuItems = actions?.map((actionType) => {
    switch (actionType) {
      case 'clear':
        return (
          <MenuItem
            data-mui-test="clear-action-button"
            onClick={() => {
              onClear();
              setAnchorEl(null);
            }}
            key={actionType}
          >
            {localeText.clearButtonLabel}
          </MenuItem>
        );

      case 'cancel':
        return (
          <MenuItem
            onClick={() => {
              setAnchorEl(null);
              onCancel();
            }}
            key={actionType}
          >
            {localeText.cancelButtonLabel}
          </MenuItem>
        );

      case 'accept':
        return (
          <MenuItem
            onClick={() => {
              setAnchorEl(null);
              onAccept();
            }}
            key={actionType}
          >
            {localeText.okButtonLabel}
          </MenuItem>
        );

      case 'today':
        return (
          <MenuItem
            data-mui-test="today-action-button"
            onClick={() => {
              setAnchorEl(null);
              onSetToday();
            }}
            key={actionType}
          >
            {localeText.todayButtonLabel}
          </MenuItem>
        );

      default:
        return null;
    }
  });

  return (
    <DialogActions className={className}>
      <Button
        id={`picker-actions-${id}`}
        aria-controls={open ? 'basic-menu' : undefined}
        aria-haspopup="true"
        aria-expanded={open ? 'true' : undefined}
        onClick={(event) => setAnchorEl(event.currentTarget)}
      >
        Actions
      </Button>
      <Menu
        id="basic-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={() => setAnchorEl(null)}
        MenuListProps={{
          'aria-labelledby': `picker-actions-${id}`,
        }}
      >
        {menuItems}
      </Menu>
    </DialogActions>
  );
}
function CustomDateTimePicker({ schedule, setSchedule }) {
  Number.prototype.padLeft = function (base, chr) {
    var len = (String(base || 10).length - String(this).length) + 1;
    return len > 0 ? new Array(len).join(chr || '0') + this : this;
  }
  const formatDateTime = (value) => {
    let schedule
    if (value !== null) {
      var d = new Date(value.$d)
      schedule = [d.getDate().padLeft(),(d.getMonth() + 1).padLeft(),
      d.getFullYear()].join('/') + ' ' +
        [d.getHours().padLeft(),
        d.getMinutes().padLeft(),
        d.getSeconds().padLeft()].join(':');
    }
    setSchedule(schedule)
  }
  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} >
      <DemoContainer components={['DateTimePicker']}
        sx={{
          "& .MuiTextField-root": {
            minWidth: "unset !important"
          }
        }}
      >
        <DateTimePicker label="Now"
          sx={{
            "& .MuiSvgIcon-root": {
              color: "white"
            },
            "& .MuiFormLabel-root": {
              color: "white",
              fontSize: "1em"
            },
            "& .MuiInputBase-input": {
              color: "white",
              fontSize: "0.9em"
            },
            "& fieldset": {
              borderColor: "white"
            }


          }}
          ampm={false}
          slots={{
            actionBar: CustomActionBar,
          }}
          slotProps={{
            actionBar: {
              actions: ['accept', 'today', 'clear', 'cancel'],
            },
          }}
          onChange={(newValue) => {
            formatDateTime(newValue)
          }}
          disablePast={true}

        />
      </DemoContainer>
    </LocalizationProvider>
  )
}

export default CustomDateTimePicker