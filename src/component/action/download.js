import { useEffect, useState } from 'react'
import { saveAs } from 'file-saver';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDownload } from '@fortawesome/free-solid-svg-icons';
import '../../sass/download.scss'

const ConfigFileGenerator = (props) => {
    var configData =[]
    const convertData = (data) =>{
        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                const value = data[key];
                configData.push(
                {
                    "key" : key,
                    "value" : value
                }
              )
            }
        }
        const comments = `# MySQL Configuration
                        # Generated by Your App
                        # Date: ${new Date().toLocaleString()} \n`;

        const configLines = configData.map(item => `${item.key}=${item.value}`).join('\n');
        const fileContent = comments + configLines;

        const blob = new Blob([fileContent], { type: 'text/plain;charset=utf-8' });
        saveAs(blob, 'config.conf');
    }
    // useEffect=()=>{
        
    // }
    // const configData = [
    //     { key: 'brokerUrl', value: 'tcp://localhost:1883' },
    //     { key: 'clientId', value: 'client1' },
    //     { key: 'cleanSession', value: 'true' },
    //     { key: 'connectionTimeOut', value: '100' },
    //     { key: 'keepAlive', value: '100' },
    //     { key: 'login', value: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbiI6IntcInBhc3N3b3JkXCI6XCIxMjM0XCIsXCJ0b3BpY1wiOlwiY2xpZW50XzEyMzQ1Njc4OVwiLFwidXNlcm5hbWVcIjpcInNlcnZlclwifSJ9.PKuvmDEcyoxCRin73PA1SiUnUax9-ZGN5eAXsh3Gv7Y' }
    // ];

    const createConfigFile = () => {
        fetch("http://localhost:8081/api/v1/downloadFile/"+props.id)
            .then(response => response.json())
            .then(data => convertData(data))
            .catch(err => console.log(err))
        console.log(configData);
    };
    return (
        <div className='downloadBtn'>
            <button onClick={createConfigFile}>
                <FontAwesomeIcon icon={faDownload} style={{ color: "#e1ff00", }} />
            </button>
        </div>
    );
}
export default ConfigFileGenerator