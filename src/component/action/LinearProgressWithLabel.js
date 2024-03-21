import * as React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import PropTypes from 'prop-types';
import LinearProgress from '@mui/material/LinearProgress';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { IP } from '../Layout/constaints';
import { Tooltip } from '@mui/material';

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
    const [progress, setProgress] = React.useState(0);
    const [restoreMessage, setRestoreMessage] = React.useState("")
    React.useEffect(() => {
        if (processId.processId.idRestore !== null) {
            let percent = 0;
            // fetch(
            //     IP +
            //     "/api/v1/info/database/restore?databaseName=" +
            //     processId.processId.databaseName +
            //     "&idRestore=" +
            //     processId.processId.idRestore
            // )
            //     .then((response) => response.json())
            //     .then((data) => {
            //         const record = data.find(item => item.id === processId.processId.idRestore)
            //         if(record!==null ) percent = record.restoreProcess;
            //         setProgress(percent);
            //         if(record!== null){
            //             if (percent === 100 || (record.restoreStatus !== "Processing")) {
            //                 setRestoreMessage(record.restoreStatus)
            //                 let restoreInfo = sessionStorage.getItem("restoreInfo")
            //                 if (restoreInfo !== null) {
            //                     let tmp = [...JSON.parse(restoreInfo)]
            //                     tmp = tmp.map(item => {
            //                         if (processId.processId.databaseName.concat(processId.processId.ipDbRunning) === item.capture_id) {
            //                             return {
            //                                 ...item,
            //                                 'idRestore': null
            //                             }
            //                         }
            //                         else return item;
            //                     })
            //                     sessionStorage.setItem("restoreInfo", JSON.stringify(tmp))
            //                 }
            //                 clearInterval(timer);
            //             }
            //         }
            //     });
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
                        const record = data.find(item => item.id === processId.processId.idRestore)
                        if (record !== undefined) percent = record.restoreProcess;
                        setProgress(percent);
                        if (record !== undefined) {
                            if (percent === 100 || (record.restoreStatus !== "Processing")) {
                                setRestoreMessage(record.restoreStatus)
                                let restoreInfo = sessionStorage.getItem("restoreInfo")
                                if (restoreInfo !== null) {
                                    let tmp = [...JSON.parse(restoreInfo)]
                                    tmp = tmp.map(item => {
                                        if (processId.processId.databaseName.concat(processId.processId.ipDbRunning) === item.capture_id) {
                                            return {
                                                ...item,
                                                'idRestore': null
                                            }
                                        }
                                        else return item;
                                    })
                                    sessionStorage.setItem("restoreInfo", JSON.stringify(tmp))
                                }
                            }
                            if(record === undefined){
                                setRestoreMessage("Error")
                            } 
                        }
                    });
            }, 1000);
            if(restoreMessage.includes("Finished") ||restoreMessage.includes("error") ) clearInterval(timer)
            return () => {
                clearInterval(timer);
            };
        }
    }, [processId]);

    return (
        <Box sx={{ width: '100%' }}>
            {processId.processId.idRestore !== null && restoreMessage==="" ?  (
                <LinearProgressWithLabel value={progress} />) : (<Tooltip title={restoreMessage}>{restoreMessage}</Tooltip>)
            }
        </Box>
    );
}
