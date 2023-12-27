
import { saveAs } from 'file-saver';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDownload } from '@fortawesome/free-solid-svg-icons';
import '../../sass/download.scss'
import { IP } from '../Layout/constaints';

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
        const headComments = `# MySQL Configuration
                        # Generated by Your App
                        # Date: ${new Date().toLocaleString()} \n`;
        const bodyComment = ["# IP của broker","#Client ID","#Clean session","# Thời gian đợi kết nối thành công","# broker sẽ tự động disconnect client khi không nhận được bất kì gói tin nào từ client trong vòng 1.5 * keepAlive","# Thông tin login broker (username, password, topic)"]
        var configLines = "";
        for(let i=0;i<configData.length;i++){
            configLines+= bodyComment[i]+'\n'
            configLines+= configData[i].key +"="+ configData[i].value +'\n'
        }
        const fileContent = headComments + configLines;

        const blob = new Blob([fileContent], { type: 'text/plain;charset=utf-8' });
        saveAs(blob, 'config.conf');
    }

    const createConfigFile = () => {
        fetch("http://"+IP+"/api/v1/downloadFile/"+props.id)
            .then(response => response.json())
            .then(data => convertData(data))
            .catch(err => console.log(err))
        console.log(configData);
    };
    return (
        <div className='downloadBtn'>
            <button onClick={createConfigFile}>
                <FontAwesomeIcon icon={faDownload} style={{ color: "#ff1ce8", }} />
            </button>
        </div>
    );
}
export default ConfigFileGenerator