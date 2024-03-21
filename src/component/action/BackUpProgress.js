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
    value: PropTypes.number.isRequired,
};

export default function BackUpProgress(processId) {
    const [progress, setProgress] = React.useState(0);
    const [backupMessage, setBackupMessage] = React.useState("")
    React.useEffect(() => {
        // const timer = setInterval(() => {
        //     fetch(IP + "/api/v1/list/infoDatabase?databaseName=" + processId.processId.databaseName)
        //         .then((response) => response.json())
        //         .then((data) => {
        //             console.log(processId)
        //             setProgress(data.backupProcess)
        //             if (data.backupProcess === 100) {
        //                 console.log(data.backupProcess)
        //                 setBackupMessage(data.backupStatus)
        //                 let restoreInfo = sessionStorage.getItem("restoreInfo")
        //                 if (restoreInfo !== null) {
        //                     let tmp = [...JSON.parse(restoreInfo)]
        //                     tmp = tmp.map(item => {
        //                         if (processId.processId.databaseName.concat(processId.processId.ipDbRunning) === item.capture_id) {
        //                             return {
        //                                 ...item,
        //                                 'idRestore': data.id ,
        //                                 'isBackuping': 0
        //                             }
        //                         }
        //                         else return item;
        //                     })
        //                     sessionStorage.setItem("restoreInfo", JSON.stringify(tmp))
        //                 }
        //             }
        //         });
        // }, 1000);
        // console.log(backupMessage,backupMessage.includes("Finished"))
        // if(backupMessage.includes("Finished") || backupMessage.includes("error")) clearInterval(timer)
        // return () => {
        //     clearInterval(timer);
        // };
        setProgress(processId.processId.backupStatus)
    }, [processId]);

    return (
        <Box sx={{ width: '100%' }}>
            {backupMessage===""?<LinearProgressWithLabel value={progress} /> :<Tooltip title={backupMessage}>{backupMessage}</Tooltip> }
            {/* <LinearProgressWithLabel value={progress} /> */}
        </Box>
    );
}
