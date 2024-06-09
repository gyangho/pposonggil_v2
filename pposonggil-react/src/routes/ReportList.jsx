// import React, { useEffect, useState } from 'react';
// import axios from 'axios';
// import styled from 'styled-components';
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faFlag } from "@fortawesome/free-solid-svg-icons";

// const Container = styled.div`
//   width: 100%;
//   height: 100%;
//   background-color: #E2E2E2;
//   display: flex;
//   flex-direction: column;
//   align-items: center;
//   padding-top: 20px;
// `;

// const Title = styled.h1`
//   margin-bottom: 20px;
//   color: #333;
//   font-weight: bold;
//   font-size: 20px;
// `;

// const ReportListForm = styled.ul`
//   width: 80%;
//   margin-top: 20px;
//   background-color: white;
//   border-radius: 10px;
//   padding: 10px;
//   box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
// `;

// const ReportItem = styled.li`
//   width: 100%;
//   padding: 10px;
//   background-color: white;
//   margin-bottom: 10px;
//   border-radius: 5px;
//   box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
//   display: flex;
//   justify-content: space-between;
//   align-items: center;
//   font-weight: bold;
// `;

// const NoReportsMessage = styled.div`
//   margin-top: 20px;
//   font-size: 18px;
//   color: #555;
//   font-weight: bold;
// `;

// function ReportList() {
//     const [reports, setReports] = useState([]);

//     useEffect(() => {
//         const fetchReports = async () => {
//             try {
//                 const myId = 1; // 실제 사용자의 ID로 대체해야 합니다.
//                 const response = await axios.get(`https://pposong.ddns.net/api/reports/by-subject/${myId}`);
//                 setReports(response.data);
//             } catch (error) {
//                 console.error('신고 목록을 불러오는 중 오류 발생:', error);
//             }
//         };

//         fetchReports();
//     }, []);

//     return (
//         <Container>
//             <Title>내가 신고한 신고 목록 <FontAwesomeIcon icon={faFlag} /></Title>
//             {reports.length === 0 ? (
//                 <NoReportsMessage>신고한 사람이 없습니다.</NoReportsMessage>
//             ) : (
//                 <ReportListForm>
//                     {reports.map((report) => (
//                         <ReportItem key={report.reportId}>
//                             {report.objectNickName} - {report.reportType}
//                         </ReportItem>
//                     ))}
//                 </ReportListForm>
//             )}
//         </Container>
//     );
// }

// export default ReportList;

import React, { useEffect, useState } from 'react';
// import axios from 'axios';
import api from "../api/api";
import styled from 'styled-components';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlag, faUser, faArrowRight } from "@fortawesome/free-solid-svg-icons";

const Container = styled.div`
  width: 100%;
  height: 100%;
  background-color: #E2E2E2;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
`;

const Title = styled.h1`
  margin-bottom: 20px;
  color: #333;
  font-weight: bold;
  font-size: 20px;
`;

const ReportListForm = styled.ul`
  width: 80%;
  margin-top: 20px;
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const ReportItem = styled.li`
  width: 100%;
  padding: 15px;
  background-color: #f9f9f9;
  margin-bottom: 15px;
  border-radius: 10px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
`;

const ReportContent = styled.div`
  display: flex;
  align-items: center;
`;

const ReportText = styled.div`
  margin-left: 10px;
`;

const NoReportsMessage = styled.div`
  margin-top: 20px;
  font-size: 18px;
  color: #555;
  font-weight: bold;
`;

// const myId = localStorage.getItem('id');

function ReportList() {
  const [reports, setReports] = useState([]);

  useEffect(() => {
    const fetchReports = async () => {
      try {
        const myId = localStorage.getItem('id');; // 실제 사용자의 ID로 대체해야 합니다.
        const response = await api.get(`https://pposong.ddns.net/api/reports/by-subject/${myId}`);
        setReports(response.data);
      } catch (error) {
        console.error('신고 목록을 불러오는 중 오류 발생:', error);
      }
    };

    fetchReports();
  }, []);

  return (
    <Container>
      <Title>내가 신고한 신고 목록 <FontAwesomeIcon icon={faFlag} /></Title>
      {reports.length === 0 ? (
        <NoReportsMessage>신고한 사람이 없습니다.</NoReportsMessage>
      ) : (
        <ReportListForm>
          {reports.map((report) => (
            <ReportItem key={report.reportId}>
              <ReportContent>
                {/* <FontAwesomeIcon icon={faUser} /> */}
                <ReportText>
                  {report.subjectNickName} <FontAwesomeIcon icon={faArrowRight} /> {report.objectNickName}
                  <br /> 신고사유 : {report.reportType}
                </ReportText>
              </ReportContent>
            </ReportItem>
          ))}
        </ReportListForm>
      )}
    </Container>
  );
}

export default ReportList;

