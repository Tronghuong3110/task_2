import * as React from 'react';
import PropTypes from 'prop-types';
import LinearProgress from '@mui/material/LinearProgress';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { IP } from '../Layout/constaints';

function LinearProgressWithLabel(props) {
    return (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Box sx={{ width: '100%', mr: 1 }}>
                <LinearProgress color='secondary' variant="determinate" {...props} />
            </Box>
            <Box sx={{ minWidth: 35 }}>
                <Typography variant="body2" sx={{ color: "white" }}>{`${Math.round(
                    props.value,
                )}%`}</Typography>
            </Box>
        </Box>
    );
}

LinearProgressWithLabel.propTypes = {
    /**
     * The value of the progress indicator for the determinate and buffer variants.
     * Value between 0 and 100.
     */
    value: PropTypes.number.isRequired,
};

export default function LinearWithValueLabel(processId) {
    const [progress, setProgress] = React.useState(10);
    React.useEffect(() => {
        let percent = 0;
        const timer = setInterval(() => {
            fetch(
                IP +
                "/api/v1/info/database/restore?databaseName=" +
                processId.processId.databaseName +
                "&idRestore=" +
                processId.processId.idRestore
            )
                .then((response) => response.json())
                .then((data) => {
                    percent = isNaN(data.restoreProcess) ? 100 : data.restoreProcess;
                    setProgress(percent);
                    if (percent === 100) {
                        clearInterval(timer);
                    }
                });
        }, 2000);

        return () => {
            clearInterval(timer);
        };
    }, []);

    return (
        <Box sx={{ width: '100%' }}>
            <LinearProgressWithLabel value={progress} />
        </Box>
    );
}
