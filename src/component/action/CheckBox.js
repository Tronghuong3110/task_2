import * as React from 'react';
import { pink } from '@mui/material/colors';
import Checkbox from '@mui/material/Checkbox';

const label = { inputProps: { 'aria-label': 'Checkbox demo' } };

const CheckBox = () => {
  return (
    <div>
      <Checkbox
        {...label}
        sx={{
          color: 'white',
          '&.Mui-checked': {
            color: 'white',
          },
        }}
      />
    </div>
  );
}
export default CheckBox;